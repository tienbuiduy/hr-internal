package com.hr.controller;

import com.hr.constant.CommonConstant;
import com.hr.model.Opportunity;
import com.hr.model.Project;
import com.hr.model.User;
import com.hr.model.dto.request.ProjectRequestDTO;
import com.hr.model.dto.request.ProjectSearchRequestDTO;
import com.hr.model.dto.response.ProjectResponseDTO;
import com.hr.service.ProjectService;
import com.hr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
	@Autowired
	private  ProjectService projectService;

	@GetMapping("/ranks")
	@ResponseBody
	public List<String> getListRanks() {
		return projectService.getListRank();
	}

	@GetMapping
	public List<ProjectResponseDTO> list(){
		return projectService.list();
	}

	@Autowired
	private UserService userService;

	@PostMapping
	public ResponseEntity<ProjectRequestDTO> insertOrUpdateProject(@Valid @RequestBody ProjectRequestDTO projectRequestDTO) {
		try {
			projectService.createOrUpdate(projectRequestDTO);
			return new ResponseEntity<Opportunity>(HttpStatus.OK).ok(projectRequestDTO);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		User loginUser = userService.getLoginUser();
		List<String> userRoleList = userService.getListRoleCodeOfUser(loginUser);
		if (userRoleList.contains(CommonConstant.RoleCode.DIVISION_MANAGER) || userRoleList.contains(CommonConstant.RoleCode.PMO) || userRoleList.contains(CommonConstant.RoleCode.PROJECT_MANAGER)) {
			try {
				projectService.delete(id, loginUser.getId());
				return new ResponseEntity<>(HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/search")
	public List<ProjectResponseDTO> search(@RequestBody ProjectSearchRequestDTO projectSearchRequestDTO){
		return projectService.search(projectSearchRequestDTO);
	}

	@GetMapping("/projectsOfLoginUser")
	public List<ProjectResponseDTO> listProjectsOfLoginUser(OAuth2AuthenticationToken authentication){
		return projectService.listProjectsOfLoginUser(authentication);
	}

	@GetMapping("/{id}")
	public Project getProject(@PathVariable Integer id)
	{
		return projectService.getProject(id);
	}
}
