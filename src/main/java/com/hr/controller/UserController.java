package com.hr.controller;

import com.hr.constant.CommonConstant;
import com.hr.model.ObjectValidation;
import com.hr.model.Role;
import com.hr.model.User;
import com.hr.model.dto.RoleDTO;
import com.hr.model.dto.request.UserRequestDTO;
import com.hr.model.dto.request.UserSearchRequestDTO;
import com.hr.model.dto.response.AppParamsResponseDTO;
import com.hr.model.dto.response.UserResponseDTO;
import com.hr.service.AppParamsService;
import com.hr.service.RoleService;
import com.hr.service.UserRoleService;
import com.hr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AppParamsService appParamsService;

    @GetMapping
    public List<UserResponseDTO> list() {
        return userService.list();
    }

    @GetMapping("/listAllContainResignedEmployee")
    public List<UserResponseDTO> listAllContainResignedEmployee() {
        return userService.listAllContainResignedEmployee();
    }

    @GetMapping("/roles")
    public List<RoleDTO> roles() {
        return roleService.getRoles();
    }

    @GetMapping("/generateCode")
    public String employeeCode() {
        return userService.generateCode();
    }


    @SuppressWarnings("static-access")
    @PostMapping
    public ResponseEntity<?> createOrUpdate(@RequestBody UserRequestDTO userDTO, OAuth2AuthenticationToken authentication) {
        List<Integer> loginUserRoleIds = userService.listRoleIdOfLoginUser(authentication);
        List<Integer> listRoleIdsContainDMAndPMO = Arrays.asList(CommonConstant.RoleId.DIVISION_MANAGER, CommonConstant.RoleId.PMO);
        try {
            List<ObjectValidation> objectValidationList = userService.validate(userDTO);
            if (!objectValidationList.isEmpty()) {
                return ResponseEntity.badRequest().body(objectValidationList);
            }

            List<Integer> currentRoleIds = userService.findRolesByUserId(userDTO.getId());

            User user = new User();

            String department = userDTO.getDepartment();
            if(department.isEmpty()){
                user.setDepartment(CommonConstant.Department.OS3);
            } else{
                user.setDepartment(department);
            }

            List<Integer> updatedRoleIds = userDTO.getRoles();

            if(currentRoleIds.contains(null) || currentRoleIds.isEmpty()){//tao moi
                if(
                        CollectionUtils.containsAny(updatedRoleIds, listRoleIdsContainDMAndPMO) &&
                                !CollectionUtils.containsAny(loginUserRoleIds, listRoleIdsContainDMAndPMO)
                ){
                    objectValidationList.add(new ObjectValidation("", CommonConstant.ErrorMessage.REQUIRE_ROLE_DM_PMO_TO_CREATE_DM_PMO));
                    return ResponseEntity.badRequest().body(objectValidationList);
                } else{
                    user = userService.createOrUpdate(userDTO);
                }
            } else { // cap nhat
                if(!currentRoleIds.equals(updatedRoleIds) &&
                        !CollectionUtils.containsAny(loginUserRoleIds, listRoleIdsContainDMAndPMO)){
                    objectValidationList.add(new ObjectValidation("", CommonConstant.ErrorMessage.REQUIRE_ROLE_DM_PMO_TO_UPDATE_ROLE));
                    return ResponseEntity.badRequest().body(objectValidationList);
                } else{
                    user = userService.createOrUpdate(userDTO);
                }
            }

            userRoleService.deleteByUser(user);


            List<Role> listRole = roleService.findByIds(updatedRoleIds);
            userRoleService.save(user, listRole);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        User loginUser = userService.getLoginUser();
        List<String> userRoleList = userService.getListRoleCodeOfUser(userService.getLoginUser());
        if (userRoleList.contains(CommonConstant.RoleCode.DIVISION_MANAGER) || userRoleList.contains(CommonConstant.RoleCode.PMO)
                || userRoleList.contains(CommonConstant.RoleCode.HR)) {
            try {
                userService.delete(id, loginUser.getId());
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(CommonConstant.ErrorMessage.NOT_AUTHORIZED, HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/search")
    public List<UserResponseDTO> search(@RequestBody UserSearchRequestDTO userSearchRequestDTO) {
        return userService.search(userSearchRequestDTO);
    }

    @GetMapping("/roles/{code}")
    public List<UserResponseDTO> getListUserByRoleCode(@PathVariable String code) {
        return userService.findAllByRoleCode(code);
    }

    @GetMapping("/status")
    public List<AppParamsResponseDTO> appParams() {
        return appParamsService.listAppParamsByType(CommonConstant.AppParams.EMPLOYEE_STATUS);
    }

    @GetMapping("/{id}")
    public User getEmployee(@PathVariable int id)
    {
        return userService.findById(id);
    }
}