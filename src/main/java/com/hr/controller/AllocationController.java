package com.hr.controller;

import com.hr.constant.CommonConstant;
import com.hr.model.Allocation;
import com.hr.model.ObjectValidation;
import com.hr.model.User;
import com.hr.model.dto.request.AllocationDetailRequestDTO;
import com.hr.model.dto.request.AllocationRequestDTO;
import com.hr.model.dto.request.AllocationSearchRequestDTO;
import com.hr.model.dto.request.EERequestDTO;
import com.hr.model.dto.response.AllocationDetailResponseDTO;
import com.hr.model.dto.response.AllocationEmployeeResponseDTO;
import com.hr.model.dto.response.AllocationResponseDTO;
import com.hr.model.dto.response.AppParamsResponseDTO;
import com.hr.service.AllocationDetailService;
import com.hr.service.AllocationService;
import com.hr.service.AppParamsService;
import com.hr.service.UserService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/allocations")
public class AllocationController {
	@Autowired
	private  AllocationService allocationService;

	@Autowired
	private AllocationDetailService allocationDetailService;

	@Autowired
	private UserService userService;

	@Autowired
	private AppParamsService appParamsService;

	@RequestMapping
	public List<AllocationResponseDTO> list(HttpSession session){
		List<AllocationResponseDTO> allocationResponseDTOS = allocationService.list();
		session.removeAttribute("allocationList");
		session.setAttribute("allocationList", allocationResponseDTOS);
		return allocationResponseDTOS;
	}

	@PostMapping
	public ResponseEntity<?> createOrUpdateAllocation(@Valid @RequestBody AllocationRequestDTO allocationRequestDTO) {
		User loginUser = userService.getLoginUser();
		try {
			if ( ( Objects.isNull(allocationRequestDTO.getProjectId()) && Objects.isNull(allocationRequestDTO.getOppId())) ||
				 ( !Objects.isNull(allocationRequestDTO.getProjectId()) && !Objects.isNull(allocationRequestDTO.getOppId()))
				) {
				List<ObjectValidation> objectValidationList = new ArrayList<>();
				ObjectValidation objectValidation = new ObjectValidation("", CommonConstant.ErrorMessage.ENTER_PROJECT_OR_OPPORTUNITY_ONLY);
				objectValidationList.add(objectValidation);
				return ResponseEntity.badRequest().body(objectValidationList);
			}
			allocationService.delete(allocationRequestDTO.getId(), loginUser.getId());
			allocationService.saveAllocationAndDetail(allocationRequestDTO);
			return new ResponseEntity<Allocation>(HttpStatus.OK).ok(allocationRequestDTO);
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
				allocationService.delete(id, loginUser.getId());
				allocationDetailService.deleteByAllocationId(id);
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
	public List<AllocationResponseDTO> search(@Valid @RequestBody AllocationSearchRequestDTO allocationSearchRequestDTO, HttpSession session) {
		List<AllocationResponseDTO> allocationResponseDTOS = allocationService.search(allocationSearchRequestDTO);
		session.removeAttribute("allocationList");
		session.setAttribute("allocationList", allocationResponseDTOS);
		return allocationResponseDTOS;
	}

	@PostMapping("/detail")
	public List<AllocationDetailResponseDTO> listAllocationDetail(@Valid @RequestBody AllocationDetailRequestDTO allocationDetailRequestDTO) {
		if (allocationDetailRequestDTO.isCheckFreeEmployee())
			return allocationService.getListFreeAllocationDetail(allocationDetailRequestDTO);
		return allocationService.getListAllocationDetail(allocationDetailRequestDTO);
	}

	@PostMapping("/employee")
	public List<AllocationEmployeeResponseDTO> listAllocationEmployee(@Valid @RequestBody AllocationDetailRequestDTO allocationDetailRequestDTO) {
		return allocationService.getListAllocationEmployee(allocationDetailRequestDTO);
	}

	@PostMapping("/import-allocation")
	public ResponseEntity<?> importAllocation(MultipartFile file) {
		String errorMessages = allocationService.checkConditionToImportAllocation();
		if (!errorMessages.isEmpty()) {
			return ResponseEntity.badRequest().body(errorMessages);
		}
		try {
			String message = allocationService.importAllocation(file);
			return new ResponseEntity<>(HttpStatus.OK).ok(message);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/upload-allo-by-month")
	public ResponseEntity<?> importAllocationByMonth(MultipartFile file) {
		String errorMessages = allocationService.checkConditionToImportAllocation();
		if (!errorMessages.isEmpty()) {
			return ResponseEntity.badRequest().body(errorMessages);
		}
		try {
			String message = allocationService.importAllocationByMonth(file);
			return new ResponseEntity<>(HttpStatus.OK).ok(message);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/import-allocation-by-day")
	public ResponseEntity<?> importAllocationByDay(MultipartFile file) {
		String errorMessages = allocationService.checkConditionToImportAllocation();
		if (!errorMessages.isEmpty()) {
			return ResponseEntity.badRequest().body(errorMessages);
		}
		try {
			String message = allocationService.importAllocationByDay(file);
			return new ResponseEntity<>(HttpStatus.OK).ok(message);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/import-allocation-by-period")
	public ResponseEntity<?> importAllocationByPeriod(MultipartFile file) {
		String errorMessages = allocationService.checkConditionToImportAllocation();
		if (!errorMessages.isEmpty()) {
			return ResponseEntity.badRequest().body(errorMessages);
		}
		try {
			String message = allocationService.importAllocationByPeriod(file);
			return new ResponseEntity<>(HttpStatus.OK).ok(message);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/export-ee")
	public ResponseEntity<?> calculateEE(@Valid @RequestBody EERequestDTO eERequestDTO, HttpServletResponse response) throws IOException, InvalidFormatException {
		List<ObjectValidation> errorMessages = allocationService.checkConditionToExportEE();
		if (!errorMessages.isEmpty()) {
			return ResponseEntity.badRequest().body(errorMessages);
		}

		ByteArrayOutputStream byteArrayOutputStream = allocationService.exportEE(eERequestDTO);
		String filePath = CommonConstant.FilePath.PROJECT_EE_EXPORT;
		File downloadFile = new File(filePath);

		String fileName = CommonConstant.FilePath.PROJECT_EE_TEMPLATE_TEMPORARY_FILE_NAME;
		byte[] file  = Files.readAllBytes(downloadFile.toPath());

		response.setHeader("Content-disposition", "attachment;filename=" + fileName);
		response.setHeader("charset", "iso-8859-1");
		response.setContentType("application/octet-stream");
		response.setContentLength(file.length);
		response.setStatus(HttpServletResponse.SC_OK);

		FileCopyUtils.copy(file, response.getOutputStream());
		response.flushBuffer();
		return ResponseEntity.ok().build();
	}
	@GetMapping("/export-ee")
	public void calculateEE(HttpServletResponse response) throws IOException {
		String filePath = CommonConstant.FilePath.PROJECT_EE_EXPORT;
		File downloadFile = new File(filePath);
		String fileName = CommonConstant.FilePath.PROJECT_EE_TEMPLATE_TEMPORARY_FILE_NAME;
		byte[] file  = Files.readAllBytes(downloadFile.toPath());
		response.setHeader("Content-disposition", "attachment;filename=" + fileName);
		response.setHeader("charset", "iso-8859-1");
		response.setContentType("application/octet-stream");
		response.setContentLength(file.length);
		response.setStatus(HttpServletResponse.SC_OK);
		FileCopyUtils.copy(file, response.getOutputStream());
		response.flushBuffer();
	}

	@GetMapping("/type")
	public List<AppParamsResponseDTO> appParams() {
		return appParamsService.listAppParamsByType(CommonConstant.AppParams.ALLOCATION_TYPE);
	}

	@GetMapping("/export/day")
	public void exportAllocationByDay(HttpSession session, HttpServletResponse response) throws IOException, ParseException {
		String errorMessages = allocationService.checkConditionToImportAllocation();
		List<AllocationResponseDTO> allocationResponseDTOS = (List<AllocationResponseDTO>)session.getAttribute("allocationList");

		ByteArrayOutputStream byteArrayOutputStream = allocationService.exportAllocationByDay(allocationResponseDTOS);
		String filePath = CommonConstant.FilePath.ALLOCATION_BY_DAY_TEMPLATE;
		File downloadFile = new File(filePath);
		String fileName = CommonConstant.FilePath.ALLOCATION_BY_DAY_EXPORT_FILE_NAME;
		byte[] file  = Files.readAllBytes(downloadFile.toPath());
		response.setHeader("Content-disposition", "attachment;filename=" + fileName);
		response.setHeader("charset", "iso-8859-1");
		response.setContentType("application/octet-stream");
		response.setContentLength(file.length);
		response.setStatus(HttpServletResponse.SC_OK);
		FileCopyUtils.copy(file, response.getOutputStream());
		response.flushBuffer();
	}

	@GetMapping("/export/day/download/xlsx")
	public void downloadAllocationbyDay(HttpServletResponse response) throws IOException {
		String filePath = CommonConstant.FilePath.ALLOCATION_BY_DAY_EXPORT;
		File downloadFile = new File(filePath);
		String fileName = CommonConstant.FilePath.ALLOCATION_BY_DAY_EXPORT_FILE_NAME;
		byte[] file  = Files.readAllBytes(downloadFile.toPath());
		response.setHeader("Content-disposition", "attachment;filename=" + fileName);
		response.setHeader("charset", "iso-8859-1");
		response.setContentType("application/octet-stream");
		response.setContentLength(file.length);
		response.setStatus(HttpServletResponse.SC_OK);
		FileCopyUtils.copy(file, response.getOutputStream());
		response.flushBuffer();
	}

	@GetMapping("/export/month")
	public void exportAllocationByMonth(HttpSession session, HttpServletResponse response) throws IOException, ParseException {
		List<AllocationResponseDTO> allocationResponseDTOS = (List<AllocationResponseDTO>)session.getAttribute("allocationList");

		ByteArrayOutputStream byteArrayOutputStream = allocationService.exportAllocationByMonth(allocationResponseDTOS);
		String filePath = CommonConstant.FilePath.ALLOCATION_BY_MONTH_TEMPLATE;
		File downloadFile = new File(filePath);
		String fileName = CommonConstant.FilePath.ALLOCATION_BY_MONTH_EXPORT_FILE_NAME;
		byte[] file  = Files.readAllBytes(downloadFile.toPath());
		response.setHeader("Content-disposition", "attachment;filename=" + fileName);
		response.setHeader("charset", "iso-8859-1");
		response.setContentType("application/octet-stream");
		response.setContentLength(file.length);
		response.setStatus(HttpServletResponse.SC_OK);
		FileCopyUtils.copy(file, response.getOutputStream());
		response.flushBuffer();
	}

	@GetMapping("/export/month/download/xlsx")
	public void downloadAllocationbyMonth(HttpServletResponse response) throws IOException {
		String filePath = CommonConstant.FilePath.ALLOCATION_BY_MONTH_EXPORT;
		File downloadFile = new File(filePath);
		String fileName = CommonConstant.FilePath.ALLOCATION_BY_MONTH_EXPORT_FILE_NAME;
		byte[] file  = Files.readAllBytes(downloadFile.toPath());
		response.setHeader("Content-disposition", "attachment;filename=" + fileName);
		response.setHeader("charset", "iso-8859-1");
		response.setContentType("application/octet-stream");
		response.setContentLength(file.length);
		response.setStatus(HttpServletResponse.SC_OK);
		FileCopyUtils.copy(file, response.getOutputStream());
		response.flushBuffer();
	}

	@GetMapping("/export/period")
	public void exportAllocationByPeriod(HttpSession session, HttpServletResponse response) throws IOException, ParseException {
		List<AllocationResponseDTO> allocationResponseDTOS = (List<AllocationResponseDTO>)session.getAttribute("allocationList");

		ByteArrayOutputStream byteArrayOutputStream = allocationService.exportAllocationByPeriod(allocationResponseDTOS);
		String filePath = CommonConstant.FilePath.ALLOCATION_BY_PERIOD_TEMPLATE;
		File downloadFile = new File(filePath);
		String fileName = CommonConstant
				.FilePath
				.ALLOCATION_BY_PERIOD_EXPORT_FILE_NAME;
		byte[] file = Files.readAllBytes(downloadFile.toPath());
		response
				.setHeader("Content-disposition", "attachment;filename=" + fileName);
		response.setHeader("charset", "iso-8859-1");
		response.setContentType("application/octet-stream");
		response.setContentLength(file.length);
		response.setStatus(HttpServletResponse.SC_OK);
		FileCopyUtils.copy(file, response.getOutputStream());
		response.flushBuffer();
	}

	@GetMapping("/export/period/download/xlsx")
	public void downloadAllocationbyPeriod(HttpServletResponse response) throws IOException {
		String filePath = CommonConstant.FilePath.ALLOCATION_BY_PERIOD_EXPORT;
		File downloadFile = new File(filePath);
		String fileName = CommonConstant.FilePath.ALLOCATION_BY_PERIOD_EXPORT_FILE_NAME;
		byte[] file = Files.readAllBytes(downloadFile.toPath());


		response.setHeader("Content-disposition", "attachment;filename="
				+
				fileName);
		response.setHeader("charset", "iso-8859-1");
		response.setContentType("application/octet-stream");
		response.setContentLength(file.length);
		response.setStatus(HttpServletResponse.SC_OK);
		FileCopyUtils.copy(file, response.getOutputStream());
		response.flushBuffer();
	}
}
