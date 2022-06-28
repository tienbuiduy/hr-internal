package com.hr.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hr.constant.CommonConstant;
import com.hr.model.GoogleUser;
import com.hr.model.ObjectValidation;
import com.hr.model.Role;
import com.hr.model.User;
import com.hr.model.dto.RoleDTO;
import com.hr.model.dto.request.UserRequestDTO;
import com.hr.model.dto.request.UserSearchRequestDTO;
import com.hr.model.dto.response.UserResponseDTO;
import com.hr.repository.UserRepository;
import com.hr.repository.UserRepositoryCustom;
import com.hr.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(rollbackOn = Exception.class)
public class UserServiceImpl implements UserService {
    private final String DEFAULT_CUSTOMER_CODE = "E000001";

    private UserRepository userRepository;

    private ModelMapper modelMapper;

    private UserRepositoryCustom userRepositoryCustom;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, UserRepositoryCustom userRepositoryCustom) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.userRepositoryCustom = userRepositoryCustom;
    }

    @Override
    public List<UserResponseDTO> search(UserSearchRequestDTO userSearchRequestDTO) {
        String email = userSearchRequestDTO.getEmail().toLowerCase().trim();
        String employeeCode = userSearchRequestDTO.getEmployeeCode().toLowerCase().trim();
        String employeeName = userSearchRequestDTO.getEmployeeName().toLowerCase().trim();
        String mainSkill = userSearchRequestDTO.getMainSkill().toLowerCase().trim();
        List<Integer> roleIdsSearchCondition = userSearchRequestDTO.getRoles();
        List<String> statusIdSearchCondition = userSearchRequestDTO.getStatus();
        String groupName = userSearchRequestDTO.getGroupName().toLowerCase().trim();
        String department = userSearchRequestDTO.getDepartment().toLowerCase().trim();

        List<User> userList = userRepository.search(email,
                employeeCode, employeeName, mainSkill, groupName, department);

        List<UserResponseDTO> userResponseDTOList = new ArrayList<>();
        for (User u : userList) {
            if (u.getListRoleId(u.getRoles()).containsAll(roleIdsSearchCondition) &&
                    (statusIdSearchCondition.isEmpty() || statusIdSearchCondition.contains(u.getStatus()))
            ) {
                userResponseDTOList.add(toResponseDto(u));
            }
        }
        return userResponseDTOList;
    }

    @Override
    public User createOrUpdate(UserRequestDTO userDTO) {
        return userRepository.save(toUser(userDTO));
    }

    @Override
    public void delete(int id, int loginUserId) {
        userRepository.deleteUser(id, loginUserId);
    }

    @Override
    public synchronized String generateCode() {
        User lastUser = userRepository.findFirstByOrderByIdDesc();
        if (lastUser == null) {
            return DEFAULT_CUSTOMER_CODE;
        }
        String code = "E" + String.format("%06d", lastUser.getId() + 1);
        return code;
    }

    @Override
    public List<User> getActiveUser() {
        return userRepository.findUserByStatus(CommonConstant.EmployeeStatus.WORKING);
    }

    @Override
    public  List<User> getListAllContainResignedEmployee(){
        return userRepository.findAll();
    }

    @Override
    public List<UserResponseDTO> findAllByRoleCode(String roleCode) {
        List<User> users = userRepository.findAllByRoleCode(roleCode.toLowerCase());
        List<UserResponseDTO> userResponseDTOs = new ArrayList<>();
        for (User u : users) {
            userResponseDTOs.add(toResponseDto(u));
        }
        return userResponseDTOs;
    }

    private User toUser(UserRequestDTO userRequestDTO) {
        User user = modelMapper.map(userRequestDTO, User.class);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        int loginUserId = getLoginUserId();
        if(userRequestDTO.getId() == 0){
            user.setCreatedAt(now);
            user.setCreatedBy(loginUserId);
        } else {
            User userOldData = userRepository.findById(userRequestDTO.getId());
            user.setCreatedBy(userOldData.getCreatedBy());
            user.setCreatedAt(userOldData.getCreatedAt());
        }
        if(Objects.isNull(user.getWorkingDay()) || user.getWorkingDay().toString().isEmpty()){
            user.setWorkingDay(now);
        }
        user.setUpdatedAt(now);
        user.setUpdatedBy(loginUserId);
        return user;
    }

    public UserResponseDTO toResponseDto(User user) {

        UserResponseDTO userResponseDTO = modelMapper.map(user, UserResponseDTO.class);
        Set<Role> roleSet = user.getRoles();
        Set<RoleDTO> roleDTOSet = new LinkedHashSet<>();
        for (Role r : roleSet) {
            roleDTOSet.add(toRoleDTO(r));
        }
        userResponseDTO.setRoles(roleDTOSet);
        userResponseDTO.setStatusName(userRepository.getStatusString(CommonConstant.AppParams.EMPLOYEE_STATUS, user.getStatus()));
        return userResponseDTO;
    }

    @Override
    public User findByEmailAndIsDeleted(String email, int isDeleted) {
        return userRepository.findByEmailAndIsDeleted(email, isDeleted);
    }

    @Override
    public User findByEmployeeName(String employeeName) {
        List<User> userList = userRepository.findUserByEmployeeName(employeeName);
        if(userList.isEmpty()){
            return null;
        }
        return userList.get(0);
    }

    @Override
    public List<User> findByListEmployeeByName(String employeeName) {
        List<User> userList = userRepository.findUserByEmployeeName(employeeName);
        if(userList.isEmpty()){
            return null;
        }
        return  userList;
    }

    @Override
    public List<ObjectValidation> validate(UserRequestDTO userDTO) {
        List<ObjectValidation> objectValidationList = new ArrayList<>();
        String email = userDTO.getEmail();
        int id = userDTO.getId();
        String emailExist = userRepository.emailExist(email, id);
        if (!Objects.isNull(emailExist) && !emailExist.isEmpty()) {
            objectValidationList.add(new ObjectValidation("email", CommonConstant.ErrorMessage.EMAIL_IS_EXIST));
        }
        return objectValidationList;
    }

    public RoleDTO toRoleDTO(Role role) {
        return modelMapper.map(role, RoleDTO.class);
    }

    @Override
    public Timestamp getTimeStampFromString(String timeStampString){
        Timestamp timestamp = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy");

            Date parsedDate;
            if(timeStampString.contains("/")){
                parsedDate = dateFormat.parse(timeStampString);
            }
            else{
                parsedDate = dateFormat1.parse(timeStampString);
            }

            timestamp = new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    @Override
    public List<String> getListRoleCodeOfUser(User user) {
        List<String> roleList = new ArrayList<>();
        for(Role role: user.getRoles()){
            roleList.add(role.getRoleCode());
        }
        return roleList;
    }

    @Override
    public List<Integer> findRolesByUserId(int employeeId) {
        return userRepositoryCustom.findRoleIdByUserId(employeeId);
    }

    @Override
    public List<Integer> listRoleIdOfLoginUser(OAuth2AuthenticationToken authentication) {
        OAuth2AuthenticatedPrincipal oauthUser = authentication.getPrincipal();
        ObjectMapper objectMap = new ObjectMapper();
        GoogleUser googleUser = objectMap.convertValue(oauthUser.getAttributes(), GoogleUser.class);
        User user = findByEmailAndIsDeleted(googleUser.getEmail(), CommonConstant.NOT_DELETED);
        List<Integer> roleIds = new ArrayList<>();
        for(Role role: user.getRoles()){
            roleIds.add(role.getId());
        }
        return roleIds;
    }

    @Override
    public User getLoginUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ObjectMapper objectMap = new ObjectMapper();
        GoogleUser googleUser = objectMap.convertValue(auth.getPrincipal(), GoogleUser.class);
        return userRepository.findByEmailAndIsDeleted(googleUser.getEmail(), CommonConstant.NOT_DELETED);
    }

    @Override
    public int getLoginUserId(){
        return getLoginUser().getId();
    }

    @Override
    public User findByEmployeeCode(String employeeCode) {
        return userRepository.findUserByEmployeeCode(employeeCode);
    }

    @Override
    public User findById(int id) {
        return userRepository.findById(id);
    }
}
