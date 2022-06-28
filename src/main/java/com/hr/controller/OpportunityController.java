package com.hr.controller;

import com.hr.constant.CommonConstant;
import com.hr.model.ObjectValidation;
import com.hr.model.User;
import com.hr.model.dto.ContractTypeDTO;
import com.hr.model.dto.request.OpportunityRequestDTO;
import com.hr.model.dto.request.OpportunitySearchRequestDTO;
import com.hr.model.dto.request.ProjectRequestDTO;
import com.hr.model.dto.response.AppParamsResponseDTO;
import com.hr.model.dto.response.OpportunityResponseDTO;
import com.hr.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {
	@Autowired
	private OpportunityService opportunityService;

	@Autowired
	private ContractTypeService contractTypeService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private AppParamsService appParamsService;

	@Autowired
	private AllocationService allocationService;

	@Autowired
	private UserService userService;

	@GetMapping
	public List<OpportunityResponseDTO> getListOportunity() {
		return opportunityService.getOppotunities();
	}

	@PostMapping
	public ResponseEntity<?> insertOrUpdateOpportunity(@Valid @RequestBody OpportunityRequestDTO opportunityRequestDTO) {
		try {
			opportunityRequestDTO.setOppName("Opp_" + opportunityRequestDTO.getOppName());
			List<ObjectValidation> objectValidationList = opportunityService.validate(opportunityRequestDTO);
			if (!objectValidationList.isEmpty()) {
				return ResponseEntity.badRequest().body(objectValidationList);
			}
			opportunityService.createOrUpdate(opportunityRequestDTO);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping("/generateCode")
	public String opportunityCode(){
		return opportunityService.generateCode();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteOppotunity(@PathVariable Integer id) {
		User loginUser = userService.getLoginUser();
		List<String> userRoleList = userService.getListRoleCodeOfUser(userService.getLoginUser());
		if (userRoleList.contains(CommonConstant.RoleCode.DIVISION_MANAGER) || userRoleList.contains(CommonConstant.RoleCode.PMO) || userRoleList.contains(CommonConstant.RoleCode.PROJECT_MANAGER)) {
			try {
				opportunityService.delete(id, loginUser.getId());
				return new ResponseEntity<>(HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/type/contracts")
	@ResponseBody
	public List<ContractTypeDTO> contractTypes() {
		return contractTypeService.getContracts();
	}

	@PostMapping("/search")
	public List<OpportunityResponseDTO> search(@Valid @RequestBody OpportunitySearchRequestDTO opportunitySearchRequestDTO){
		return opportunityService.search(opportunitySearchRequestDTO);
	}

	@PostMapping("/toProject")
	public ResponseEntity<?> transferOpportunityToProject(@Valid @RequestBody ProjectRequestDTO projectRequestDTO){
		try {
			opportunityService.updateOpportunityStatus(projectRequestDTO.getOppId() , CommonConstant.OpportunityStatus.WIN);
			projectService.createOrUpdate(projectRequestDTO);
			allocationService.updateAllocationOfOpportunityToProject(projectRequestDTO.getOppId(), projectService.getLatestProjectId());
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/appParams")
	public List<AppParamsResponseDTO> appParams(){
		return appParamsService.listAppParamsByType(CommonConstant.AppParams.OPPORTUNITY_STATUS);
	}
}
