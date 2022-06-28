package com.hr.service;

import com.hr.model.ObjectValidation;
import com.hr.model.User;
import com.hr.model.dto.request.UserRequestDTO;
import com.hr.model.dto.request.UserSearchRequestDTO;
import com.hr.model.dto.response.UserResponseDTO;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

public interface UserService {
	default List<UserResponseDTO> list() {
		List<User> users = getActiveUser();
		return users.stream().map(this::toResponseDto).collect(Collectors.toList());
	}

	default List<UserResponseDTO> listAllContainResignedEmployee() {
		List<User> users = getListAllContainResignedEmployee();
		return users.stream().map(this::toResponseDto).collect(Collectors.toList());
	}

	List<UserResponseDTO> search(UserSearchRequestDTO userSearchRequestDTO);

	User createOrUpdate(UserRequestDTO userDTO) throws Exception;

	List<User> getActiveUser();

	List<User> getListAllContainResignedEmployee();

	UserResponseDTO toResponseDto(User user);

	void delete(int id, int loginUserId);

	String generateCode();

	List<UserResponseDTO> findAllByRoleCode(String roleCode);

	User findByEmailAndIsDeleted(String email, int isDeleted);

	List<ObjectValidation> validate(UserRequestDTO userDTO);

	User findByEmployeeName(String employeeName);

	List<User> findByListEmployeeByName(String employeeName);

	Timestamp getTimeStampFromString(String timeStampString);

	List<String> getListRoleCodeOfUser(User user);

	List<Integer> findRolesByUserId(int employeeId);

	List<Integer> listRoleIdOfLoginUser(OAuth2AuthenticationToken authentication);

	User getLoginUser();

	int getLoginUserId();

	User findByEmployeeCode(String employeeCode);

	User findById(int id );

}
