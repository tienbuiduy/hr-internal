package com.hr.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hr.constant.CommonConstant;
import com.hr.model.*;
import com.hr.model.dto.request.AllocationDetailRequestDTO;
import com.hr.model.dto.request.AllocationRequestDTO;
import com.hr.model.dto.request.AllocationSearchRequestDTO;
import com.hr.model.dto.request.EERequestDTO;
import com.hr.model.dto.response.*;
import com.hr.repository.*;
import com.hr.service.AllocationDetailService;
import com.hr.service.AllocationService;
import com.hr.service.UserService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Service
@Transactional(rollbackOn = Exception.class)
public class AllocationServiceImpl implements AllocationService {
    List<String> expectAllocationTitle = Arrays.asList(
            "#",
            "Employee",
            "Work Email",
            "Department",
            "Start Date",
            "End Date",
            "Project Role",
            "Allocation (%)",
            "Performance rate",
            "Qualified",
            "Note",
            "Project",
            "Nguồn",
            "Allocation type");

    List<String> expectAllocationTitleByMonth = Arrays.asList(
            "Allocation type",
            "Tên dự án",
            "Mã nhân viên",
            "Role",
            "Name",
            "Performance",
            "Đơn vị allocation");

    List<String> expectAllocationTitleByDay = Arrays.asList(
            "Mã nhân viên",
            "Tên nhân viên",
            "Tên dự án",
            "Project role",
            "Performance rate",
            "Đơn vị");

    List<String> expectAllocationTitleByPeriod = Arrays.asList(
            "Mã nhân viên",
            "Tên nhân viên",
            "Tên dự án",
            "Đơn vị");

    private ModelMapper modelMapper;

    private AllocationRepository allocationRepository;

    private UserRepository userRepository;

    private ProjectRepository projectRepository;

    private RoleRepository roleRepository;

    private AllocationRepositoryCustom allocationRepositoryCustom;

    private AllocationDetailService allocationDetailService;

    private UserService userService;

    private OpportunityRepository opportunityRepository;

    private AllocationDetailRepository allocationDetailRepository;

    static Timestamp startDate = null;
    static Timestamp endDate = null;

    public AllocationServiceImpl(AllocationRepository allocationRepository, UserRepository userRepository,
                                 ProjectRepository projectRepository, RoleRepository roleRepository, AllocationRepositoryCustom allocationRepositoryCustom,
                                 AllocationDetailService allocationDetailService, UserService userService, OpportunityRepository opportunityRepository,
                                 AllocationDetailRepository allocationDetailRepository, ModelMapper modelMapper) {
        this.allocationRepository = allocationRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.roleRepository = roleRepository;
        this.allocationRepositoryCustom = allocationRepositoryCustom;
        this.allocationDetailService = allocationDetailService;
        this.userService = userService;
        this.opportunityRepository = opportunityRepository;
        this.allocationDetailRepository = allocationDetailRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<AllocationResponseDTO> list() {
        return allocationRepositoryCustom.list();
    }

    @Override
    public void save(AllocationRequestDTO allocationRequestDTO) {
        allocationRepository.save(toAllocation(allocationRequestDTO));
    }

    @Override
    public void saveAllocationAndDetail(AllocationRequestDTO allocationRequestDTO) {
        Allocation savedAllocation = allocationRepository.save(toAllocation(allocationRequestDTO));
        saveAllocationDetail(savedAllocation, userService.getLoginUser());
    }

    @Override
    public void delete(int id, int loginUserId) {
        allocationRepository.delete(id, loginUserId);
    }

    @Override
    public List<AllocationResponseDTO> search(AllocationSearchRequestDTO allocationSearchRequestDTO) {
        String employeeName = ((allocationSearchRequestDTO.getEmployeeName() != null) ? allocationSearchRequestDTO.getEmployeeName().trim().toLowerCase() : "");
        List<Integer> roleIdsConditionSearch = allocationSearchRequestDTO.getRoles();
        String projectName = ((allocationSearchRequestDTO.getProjectName() != null) ? allocationSearchRequestDTO.getProjectName().trim().toLowerCase() : "");
        String pmName = ((allocationSearchRequestDTO.getPmName() != null) ? allocationSearchRequestDTO.getPmName().trim().toLowerCase() : "");

        float startRate = allocationSearchRequestDTO.getStartRate() > 0 ? allocationSearchRequestDTO.getStartRate() : 0;
        float endRate = allocationSearchRequestDTO.getEndRate() > 0 ? allocationSearchRequestDTO.getEndRate() : 100;

        Timestamp fromStartDate =
                ((allocationSearchRequestDTO.getFromStartDate() != null) ?
                        getDayAfterATimestamp(allocationSearchRequestDTO.getFromStartDate(), -1) : (allocationSearchRequestDTO.getFromStartDate()));
        Timestamp toStartDate = ((allocationSearchRequestDTO.getToStartDate() != null) ?
                getDayAfterATimestamp(allocationSearchRequestDTO.getToStartDate(), 1) : allocationSearchRequestDTO.getToStartDate());
        Timestamp fromEndDate =
                ((allocationSearchRequestDTO.getFromEndDate() != null) ?
                        getDayAfterATimestamp(allocationSearchRequestDTO.getFromEndDate(), -1) : (allocationSearchRequestDTO.getFromEndDate()));
        Timestamp toEndDate = ((allocationSearchRequestDTO.getToEndDate() != null) ?
                getDayAfterATimestamp(allocationSearchRequestDTO.getToEndDate(), 1) : allocationSearchRequestDTO.getToEndDate());
        List<String> typeToSearch = allocationSearchRequestDTO.getType();
        List<AllocationResponseDTO> allocationResponseDTOS = allocationRepositoryCustom.search(employeeName, projectName, pmName, fromStartDate, toStartDate, fromEndDate, toEndDate, startRate, endRate, typeToSearch);

        List<AllocationResponseDTO> allocationResponseDTOSWithRole = new ArrayList<>();
        for (AllocationResponseDTO allocationResponseDTO : allocationResponseDTOS) {
            int roleIdInProject = allocationResponseDTO.getRoleId();
            if (roleIdsConditionSearch == null || roleIdsConditionSearch.isEmpty() || roleIdsConditionSearch.contains(roleIdInProject)) {
                allocationResponseDTOSWithRole.add(allocationResponseDTO);
            }
        }
        return allocationResponseDTOSWithRole;
    }

    @Override
    public List<AllocationDetailResponseDTO> getListAllocationDetail(AllocationDetailRequestDTO allocationDetailRequestDTO) {
        List<Integer> roleIds = allocationDetailRequestDTO.getRoles();
        String employeeName = allocationDetailRequestDTO.getEmployeeName().trim().toLowerCase();
        String pmName = allocationDetailRequestDTO.getPmName().trim().toLowerCase();
        String projectName = allocationDetailRequestDTO.getProjectName().trim().toLowerCase();
        Timestamp startDate = allocationDetailRequestDTO.getStartDate();
        Timestamp endDate = allocationDetailRequestDTO.getEndDate();
        return allocationRepositoryCustom.getListAllocationDetail(employeeName, projectName,
                roleIds, pmName, startDate, endDate);
    }

    @Override
    public List<AllocationDetailResponseDTO> getListFreeAllocationDetail(AllocationDetailRequestDTO allocationDetailRequestDTO) {
        List<Integer> roleIds = allocationDetailRequestDTO.getRoles();
        String employeeName = allocationDetailRequestDTO.getEmployeeName().trim().toLowerCase();
        String pmName = allocationDetailRequestDTO.getPmName().trim().toLowerCase();
        String projectName = allocationDetailRequestDTO.getProjectName().trim().toLowerCase();
        Timestamp startDate = allocationDetailRequestDTO.getStartDate();
        Timestamp endDate = allocationDetailRequestDTO.getEndDate();
        return allocationRepositoryCustom.getListFreeAllocationDetail(employeeName, projectName, roleIds, pmName, startDate, endDate);
    }

    @Override
    public List<AllocationEmployeeResponseDTO> getListAllocationEmployee(AllocationDetailRequestDTO allocationDetailRequestDTO) {
        List<AllocationEmployeeResponseDTO> allocationEmployeeResponseDTO = allocationRepositoryCustom.
                getListAllocationEmployee(allocationDetailRequestDTO.getProjectName().toLowerCase(), allocationDetailRequestDTO.getEmployeeName().toLowerCase());
        return allocationEmployeeResponseDTO;
    }

    @Override
    public void updateAllocationOfOpportunityToProject(int oppId, int projectId) {
        allocationRepository.updateAllocationOfOpportunityToProject(oppId, projectId);
        allocationDetailRepository.updateAllocationDetailOfOpportunityToProject(oppId, projectId);
    }

    @Override
    public boolean isDuplicateAllocation(int employeeId, int projectId, Timestamp startDate, Timestamp endDate) {
        return allocationRepositoryCustom.findAllocationByEmployeeIdAndProjectIdAndStartDateAndEndDate(employeeId, projectId, startDate, endDate);
    }

    @Override
    public String importAllocationByMonth(MultipartFile multipartFile) throws IOException {
        String message = "";

        String uploadDir = CommonConstant.FilePath.ALLOCATION_IMPORT_PATH;

        Path copyLocation = Paths
                .get(uploadDir + File.separator + StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename())));
        Files.copy(multipartFile.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
        File fileData = new File(copyLocation.toString());

        FileInputStream inputStream = new FileInputStream(fileData);

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();

        Row rowFirst = sheet.getRow(0);
        if (!validateTitleRow(rowFirst, CommonConstant.Template.ALLOCATION_BY_MONTH_IMPORT_TEMPLATE_NUMBER_OF_TITLE_ELEMENTS, expectAllocationTitleByMonth)) {
            return CommonConstant.ErrorMessage.INVALID_ALLOCATION_BY_MONTH_IMPORT_TEMPLATE;
        }
        List<Calendar> listCalendars = new ArrayList<>();
        List<Cell> cellListRowFirst = new ArrayList<>();
        int lastCellNum = 0;
        for (int count = 0; count < rowFirst.getLastCellNum(); count++) {
            cellListRowFirst.add(rowFirst.getCell(count, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
            lastCellNum = count + 1;
            if (rowFirst.getCell(count) == null || rowFirst.getCell(count).toString().equals("")) {
                break;
            }
        }
        for (int i = CommonConstant.Template.ALLOCATION_BY_MONTH_IMPORT_TEMPLATE_NUMBER_OF_TITLE_ELEMENTS; i < lastCellNum; i++) {
            Calendar calendar = GregorianCalendar.getInstance();
            Date startDate = getDateFromCellWithFormatMyyyy(cellListRowFirst.get(i));
            calendar.setTime(startDate);
            listCalendars.add(calendar);
        }
        rowIterator.next();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            List<Cell> cellList = new ArrayList<>();
            for (int count = 0; count < lastCellNum; count++) {
                cellList.add(row.getCell(count, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
            }
            if (Objects.isNull(row.getCell(0)) || row.getCell(0).toString().equals("")) {
                return message;
            }
            String alloType = Objects.isNull(cellList.get(0)) ? "" : cellList.get(0).getStringCellValue();
            String projectName = Objects.isNull(cellList.get(1)) ? "" : cellList.get(1).getStringCellValue();
            String employeeCode = Objects.isNull(cellList.get(2)) ? "" : cellList.get(2).getStringCellValue();
            String roleName = Objects.isNull(cellList.get(3)) ? "" : cellList.get(3).getStringCellValue();
            String employeeName = Objects.isNull(cellList.get(4)) ? "" : cellList.get(4).getStringCellValue();
            String performance = Objects.isNull(cellList.get(6)) ? "" : cellList.get(6).getStringCellValue();
            float performanceRate = (float) cellList.get(5).getNumericCellValue();

            User user = null;
            List<Project> projects = projectRepository.findByExactProjectName(projectName.toLowerCase());
            List<Role> roles = roleRepository.findByExactRoleCodeOrRoleName(roleName.toLowerCase());
            if (employeeCode != "") {
                user = userService.findByEmployeeCode(employeeCode);
            } else {
                List<User> listUser = userService.findByListEmployeeByName(employeeName);

                if (Objects.isNull(listUser)) {
                    message = message.concat(employeeName + " not exits \n");
                    continue;
                }
                if (listUser.size() > 1) {
                    message = message.concat(employeeName + " duplicate \n");
                    continue;
                } else {
                    user = listUser.get(0);
                }

            }

            if (Objects.isNull(user)) {
                message = message.concat(employeeName + " not exist \n");
                continue;
            }

            if (projects.isEmpty()) {
                message = message.concat(projectName + " not exist \n");
                continue;
            }

            if (projects.size() > 1) {
                message = message.concat("project with name "+ projectName + " is too popular to detect \n");
                continue;
            }

            if (Objects.isNull(roles)) {
                message = message.concat(roleName + " not exist \n");
                continue;
            }
            if (roles.size() > 1) {
                message = message.concat(roleName + " dulicate \n");
                continue;
            }
            Calendar calendarPrevious = null;
            String allocationPrevious = null;
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            ObjectMapper objectMap = new ObjectMapper();
            GoogleUser googleUser = objectMap.convertValue(auth.getPrincipal(), GoogleUser.class);
            User loginUser = userRepository.findByEmailAndIsDeleted(googleUser.getEmail(), CommonConstant.NOT_DELETED);

            for (int column = CommonConstant.Template.ALLOCATION_BY_MONTH_IMPORT_TEMPLATE_NUMBER_OF_TITLE_ELEMENTS; column <= lastCellNum; column++) {
                if (column == lastCellNum) {
                    Allocation allocation = setAllocation(performance, allocationPrevious, user, alloType, roles, projects.get(0), startDate, endDate, performanceRate);
                    allocationRepository.save(allocation);
                    saveAllocationDetail(allocation, loginUser);
                    continue;
                }
                int index = column - CommonConstant.Template.ALLOCATION_BY_MONTH_IMPORT_TEMPLATE_NUMBER_OF_TITLE_ELEMENTS;
                String allocationCurrent = row.getCell(column).toString();
                Calendar calendarCurrent = listCalendars.get(index);
                calendarCurrent.set(Calendar.DAY_OF_MONTH, calendarCurrent.getActualMinimum(Calendar.DAY_OF_MONTH));

                if (column == CommonConstant.Template.ALLOCATION_BY_MONTH_IMPORT_TEMPLATE_NUMBER_OF_TITLE_ELEMENTS) {
                    setDate(calendarCurrent);
                    calendarPrevious = calendarCurrent;
                    allocationPrevious = allocationCurrent;
                    continue;
                }

                if (checkConsecutiveMonth(calendarCurrent, calendarPrevious)) {
                    if (equalAllocation(allocationCurrent, allocationPrevious)) {
                        Calendar calendar = (Calendar) calendarCurrent.clone();
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                        endDate = new Timestamp(calendar.getTimeInMillis());
                        calendarPrevious = calendarCurrent;
                        allocationPrevious = allocationCurrent;
                    } else {
                        Allocation allocation = setAllocation(performance, allocationPrevious, user, alloType, roles, projects.get(0), startDate, endDate, performanceRate);
                        allocationRepository.save(allocation);
                        saveAllocationDetail(allocation, loginUser);
                        setDate(calendarCurrent);
                        calendarPrevious = calendarCurrent;
                        allocationPrevious = allocationCurrent;
                    }
                } else {
                    Allocation allocation = setAllocation(performance, allocationPrevious, user, alloType, roles, projects.get(0), startDate, endDate, performanceRate);
                    allocationRepository.save(allocation);
                    saveAllocationDetail(allocation, loginUser);
                    setDate(calendarCurrent);
                    calendarPrevious = calendarCurrent;
                    allocationPrevious = allocationCurrent;
                }
            }
        }
        return message.isEmpty() ? CommonConstant.ErrorMessage.IMPORT_ALLOCATION_SUCCESS : CommonConstant.ErrorMessage.PREFIX_IMPORT_ERROR + message;
    }

    @Override
    public String importAllocationByDay(MultipartFile multipartFile) throws IOException, ParseException {
        String message = "";

        String uploadDir = CommonConstant.FilePath.ALLOCATION_IMPORT_PATH;

        Path copyLocation = Paths
                .get(uploadDir + File.separator + StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename())));
        Files.copy(multipartFile.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

        File fileData = new File(copyLocation.toString());

        FileInputStream inputStream = new FileInputStream(fileData);

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();

        Row rowFirst = sheet.getRow(0);
        if (!validateTitleRow(rowFirst, CommonConstant.Template.ALLOCATION_BY_DAY_IMPORT_TEMPLATE_NUMBER_OF_TITLE_ELEMENTS, expectAllocationTitleByDay)) {
            return CommonConstant.ErrorMessage.INVALID_ALLOCATION_BY_DAY_IMPORT_TEMPLATE;
        }

        int lastCellNum = rowFirst.getLastCellNum();
        List<AllocationByDay> allocationByDays = new ArrayList<>();
        for (int i = CommonConstant.Template.ALLOCATION_BY_DAY_IMPORT_TEMPLATE_NUMBER_OF_TITLE_ELEMENTS; i < lastCellNum; i++) {
            allocationByDays.add(new AllocationByDay(i, getDateFromCell(rowFirst.getCell(i))));
        }

        rowIterator.next();
        int loginUserId = userService.getLoginUserId();
        Timestamp timestampNow = new Timestamp(new Date().getTime());
        int recordNo = 0;

        while (rowIterator.hasNext()) {
            recordNo++;
            Row row = rowIterator.next();
            List<Cell> cellList = new ArrayList<>();
            for (int count = 0; count < lastCellNum; count++) {
                cellList.add(row.getCell(count, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
            }
            if (cellList.isEmpty() || cellList.size() < 4) {
                break;
            }
            String employeeCode = Objects.isNull(cellList.get(0)) ? "" : cellList.get(0).getStringCellValue().trim();
            String employeeName = Objects.isNull(cellList.get(1)) ? "" : cellList.get(1).getStringCellValue().trim();
            String projectName = Objects.isNull(cellList.get(2)) ? "" : cellList.get(2).getStringCellValue().trim();
            String projectRole = Objects.isNull(cellList.get(3)) ? "" : cellList.get(3).getStringCellValue().trim();
            float rate = Objects.isNull(cellList.get(4)) ? 0 : (float) cellList.get(4).getNumericCellValue();
            String unit = Objects.isNull(cellList.get(5)) ? "" : cellList.get(5).getStringCellValue();

            List<User> userList = (employeeCode + employeeName).isEmpty() ? new ArrayList<>() : userRepository
                    .findUserByEmployeeCodeAndEmployeeName(employeeCode.toLowerCase(), employeeName.toLowerCase());
            List<Project> projects = projectRepository.findByExactProjectName(projectName.toLowerCase());
            Opportunity opportunity = opportunityRepository.findByExactOpportunityName(projectName.toLowerCase());
            List<Role> roleList = roleRepository.findByExactRoleCodeOrRoleName(projectRole.toLowerCase());
            String messageErrorOfEachRecord = "";
            if (userList.isEmpty()) {
                messageErrorOfEachRecord = messageErrorOfEachRecord.concat("employee with code " + employeeCode + " and name " + employeeName + " not exist \n");
            }

            if (userList.size() > 1) {
                messageErrorOfEachRecord = messageErrorOfEachRecord.concat("employee with empty code" + " and name " + employeeName + " is too popular to detect \n");
            }

            if (projects.isEmpty() && Objects.isNull(opportunity)) {
                messageErrorOfEachRecord = messageErrorOfEachRecord.concat("project " + projectName + " not exist \n");
            }

            if (!projects.isEmpty() && !Objects.isNull(opportunity)) {
                messageErrorOfEachRecord = messageErrorOfEachRecord.concat("Exist both project and opportunity with name " + projectName + " \n");
            }

            if (projects.size() > 1) {
                messageErrorOfEachRecord = messageErrorOfEachRecord.concat("project with name " + projectName + " is too popular\n");
            }

            if (roleList.isEmpty()) {
                messageErrorOfEachRecord = messageErrorOfEachRecord.concat("role " + projectRole + " not exist \n");
            }
            if (roleList.size() > 1) {
                messageErrorOfEachRecord = messageErrorOfEachRecord.concat("role " + projectRole + " is too popular to detect \n");
            }

            if (!messageErrorOfEachRecord.isEmpty()) {
                message = message + "record no." + recordNo + ": " + messageErrorOfEachRecord;
                continue;
            }

            User user = userList.get(0);
            Role role = roleList.get(0);
            float coefficient = 1;
            if (unit.equals("h")) {
                coefficient = (float) 1 / 8;
            }

            List<AllocationByDay> allocationByDaysClone = new ArrayList<>(allocationByDays);
            for (int i = 6; i < lastCellNum; i++) {
                float allo = Objects.isNull(cellList.get(i)) ? 0 : (float) cellList.get(i).getNumericCellValue() * coefficient;
                allocationByDaysClone.get(i - 6).setAllo(allo);
            }

            Predicate<AllocationByDay> byAllo = allocationByDay -> allocationByDay.getAllo() > 0;
            allocationByDaysClone = allocationByDaysClone.stream().filter(byAllo).sorted(Comparator.comparing(AllocationByDay::getAlloDate)).collect(Collectors.toList());

            Timestamp startDate = new Timestamp(allocationByDaysClone.get(0).getAlloDate().getTime());
            Timestamp endDate = new Timestamp(allocationByDaysClone.get(allocationByDaysClone.size() - 1).getAlloDate().getTime());

            List<Allocation> existedAllocationWithSameEmployeeAndProjectAndRoleAndWorkingDate = allocationRepository
                    .findByEmployeeAndProjectAndRoleAndWorkingDate(user.getId(), projects.get(0).getId(), role.getId(), startDate, endDate);

            if (!existedAllocationWithSameEmployeeAndProjectAndRoleAndWorkingDate.isEmpty()) {
                message = message.concat("record no. " + recordNo).concat(": Allocation with employee " + employeeName + " - project " + projectName +
                        " - role " + role.getRoleName() + " - start date " + timeStampToString(startDate) + " - end date " + timeStampToString(endDate) + " is existed\n");
                continue;
            }
            Allocation allocation = new Allocation(user, projects.get(0), opportunity, role, allocationByDaysClone.get(0).getAllo(),
                    startDate, endDate, rate, "", "OPMS", CommonConstant.AllocationType.PLAN);

            allocation.setCreatedBy(loginUserId);
            allocation.setUpdatedBy(loginUserId);

            allocation.setCreatedAt(timestampNow);
            allocation.setUpdatedAt(timestampNow);

            allocationRepository.save(allocation);
        }
        return message.isEmpty()? CommonConstant.ErrorMessage.IMPORT_ALLOCATION_SUCCESS : CommonConstant.ErrorMessage.PREFIX_IMPORT_ERROR + message;
    }

    @Override
    public String importAllocationByPeriod(MultipartFile multipartFile) throws IOException, ParseException {
        User loginUser = userService.getLoginUser();
        Timestamp timestampNow = new Timestamp(new Date().getTime());

        String message = "";

        String uploadDir = CommonConstant.FilePath.ALLOCATION_IMPORT_PATH;

        Path copyLocation = Paths
                .get(uploadDir + File.separator + StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename())));
        Files.copy(multipartFile.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

        File fileData = new File(copyLocation.toString());

        FileInputStream inputStream = new FileInputStream(fileData);

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();

        Row titleRow = rowIterator.next();
        if (!validateTitleRow(titleRow, CommonConstant.Template.ALLOCATION_BY_PERIOD_IMPORT_TEMPLATE_NUMBER_OF_TITLE_ELEMENTS, expectAllocationTitleByPeriod)) {
            return CommonConstant.ErrorMessage.INVALID_ALLOCATION_BY_PERIOD_IMPORT_TEMPLATE;
        }

        int lastCellNum = titleRow.getLastCellNum();
        List<AllocationByPeriod> allocationByPeriods = new ArrayList<>();

        for (int i = CommonConstant.Template.ALLOCATION_BY_PERIOD_IMPORT_TEMPLATE_NUMBER_OF_TITLE_ELEMENTS; i <= lastCellNum; i++) {
            if(Objects.isNull(titleRow.getCell(i)) || titleRow.getCell(i).getStringCellValue().isEmpty()){
                lastCellNum = i - 1;
                break;
            }
            AllocationByPeriod allocationByPeriod = new AllocationByPeriod();
            String startEndDateString = titleRow.getCell(i).getStringCellValue();
            allocationByPeriod = getStartDateAndEndDateForAllocationPeriodFromString(allocationByPeriod, startEndDateString);
            allocationByPeriod.setColumnId(i);
            allocationByPeriods.add(allocationByPeriod);
        }

        int recordNo = 0;

        while (rowIterator.hasNext()) {
            recordNo++;
            Row row = rowIterator.next();
            List<Cell> cellList = new ArrayList<>();
            for (int count = 0; count <= lastCellNum; count++) {
                cellList.add(row.getCell(count, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
            }
            if (cellList.isEmpty() || cellList.size() < 4) {
                break;
            }
            String employeeCode = Objects.isNull(cellList.get(0)) ? "" : cellList.get(0).getStringCellValue().trim();
            String employeeName = Objects.isNull(cellList.get(1)) ? "" : cellList.get(1).getStringCellValue().trim();
            String projectName = Objects.isNull(cellList.get(2)) ? "" : cellList.get(2).getStringCellValue().trim();
            String projectRole = "Developer";
            String unit = Objects.isNull(cellList.get(3)) ? "" : cellList.get(3).getStringCellValue();

            List<User> userList = (employeeCode + employeeName).isEmpty() ? new ArrayList<>() : userRepository.findUserByEmployeeCodeAndEmployeeName(employeeCode.toLowerCase(), employeeName.toLowerCase());
            List<Project> projects = projectRepository.findByExactProjectName(projectName.toLowerCase());
            Opportunity opportunity = opportunityRepository.findByExactOpportunityName(projectName.toLowerCase());
            List<Role> roleList = roleRepository.findByExactRoleCodeOrRoleName(projectRole.toLowerCase());
            String messageErrorOfEachRecord = "";
            if (userList.isEmpty()) {
                messageErrorOfEachRecord = messageErrorOfEachRecord.concat("employee with code " + employeeCode + " and name " + employeeName + " not exist \n");
            }

            if (userList.size() > 1) {
                messageErrorOfEachRecord = messageErrorOfEachRecord.concat("employee with empty code" + " and name " + employeeName + " is too popular to detect \n");
            }

            if (projects.isEmpty() && Objects.isNull(opportunity)) {
                messageErrorOfEachRecord = messageErrorOfEachRecord.concat("project " + projectName + " not exist \n");
            }

            if (!projects.isEmpty() && !Objects.isNull(opportunity)) {
                messageErrorOfEachRecord = messageErrorOfEachRecord.concat("Exist both project and opportunity with name " + projectName+" \n");
            }

            if (projects.size() > 1) {
                messageErrorOfEachRecord = messageErrorOfEachRecord.concat("project with name " + projectName+" is too popular to detect\n");
            }

            if (roleList.isEmpty()) {
                messageErrorOfEachRecord = messageErrorOfEachRecord.concat("role " + projectRole + " not exist \n");
            }
            if (roleList.size() > 1) {
                messageErrorOfEachRecord = messageErrorOfEachRecord.concat("role " + projectRole + " is too popular to detect \n");
            }

            if(!messageErrorOfEachRecord.isEmpty()){
                message = message + "record no." + recordNo +": "+ messageErrorOfEachRecord;
                continue;
            }

            User user = userList.get(0);
            Role role = roleList.get(0);
            float coefficient = 1;
            if (unit.equals("h")) {
                coefficient = (float) 1 / 8;
            }

            List<AllocationByPeriod> allocationByPeriodsClone = new ArrayList<>(allocationByPeriods);
            for (int i = 4; i < lastCellNum; i++) {
                float allo = Objects.isNull(cellList.get(i)) ? 0 : (float) cellList.get(i).getNumericCellValue() * coefficient;
                allocationByPeriodsClone.get(i - 4).setAllo(allo);
            }

            int allocationByPeriodsCloneSize = allocationByPeriodsClone.size();

            for(int i = allocationByPeriodsCloneSize -1; i > 0; i-- ){
                if(allocationByPeriodsClone.get(i).getAllo() == allocationByPeriodsClone.get(i-1).getAllo()){
                    int colId = allocationByPeriodsClone.get(i-1).getColumnId();
                    Date endDate = allocationByPeriodsClone.get(i).getAlloEndDate();
                    allocationByPeriodsClone.remove(i);
                    allocationByPeriodsClone.stream()
                            .filter(obj->obj.getColumnId()== colId)
                            .findFirst()
                            .ifPresent(o->o.setAlloEndDate(endDate));
                }
            }

            for(AllocationByPeriod allocationByPeriod: allocationByPeriodsClone){
                Allocation allocation = new Allocation();
                allocation.setUser(user);
                allocation.setProject(projects.get(0));
                allocation.setOpportunity(opportunity);
                allocation.setRole(role);
                allocation.setAllo(allocationByPeriod.getAllo());
                Timestamp startDate = new Timestamp(allocationByPeriod.getAlloStartDate().getTime());
                Timestamp endDate = new Timestamp(allocationByPeriod.getAlloEndDate().getTime());
                allocation.setStartDate(startDate);
                allocation.setEndDate(endDate);
                allocation.setRate(0);
                allocation.setNote("");
                allocation.setSource("OPMS");
                allocation.setType(CommonConstant.AllocationType.PLAN);
                allocation.setCreatedAt(timestampNow);
                allocation.setUpdatedAt(timestampNow);
                allocation.setCreatedBy(loginUser.getId());
                allocation.setUpdatedBy(loginUser.getId());

                List<Allocation> existedAllocationWithSameEmployeeAndProjectAndRoleAndWorkingDate = allocationRepository
                        .findByEmployeeAndProjectAndRoleAndWorkingDate(user.getId(), projects.get(0).getId(), role.getId(), startDate, endDate);

                if (!existedAllocationWithSameEmployeeAndProjectAndRoleAndWorkingDate.isEmpty()) {
                    message = message.concat("record no. " + recordNo).concat(": Allocation with employee " + employeeName + " - project " + projectName +
                            " - role " + role.getRoleName() + " - start date " + timeStampToString(startDate) + " - end date " + timeStampToString(endDate) + " is existed\n");
                    continue;
                }

                allocationRepository.save(allocation);
                saveAllocationDetail(allocation, loginUser);
            }
        }
        return message.isEmpty()? CommonConstant.ErrorMessage.IMPORT_ALLOCATION_SUCCESS : CommonConstant.ErrorMessage.PREFIX_IMPORT_ERROR + message;
    }

    @Override
    public String importAllocation(MultipartFile multipartFile) throws IOException {

        String message = "";

        String uploadDir = CommonConstant.FilePath.ALLOCATION_IMPORT_PATH;

        Path copyLocation = Paths
                .get(uploadDir + File.separator + StringUtils.cleanPath(multipartFile.getOriginalFilename()));
        Files.copy(multipartFile.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

        File fileData = new File(copyLocation.toString());

        FileInputStream inputStream = new FileInputStream(fileData);

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

        XSSFSheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();

        Row titleRow = rowIterator.next();

        if (!validateTitleRow(titleRow, CommonConstant.Template.ALLOCATION_IMPORT_TEMPLATE_ROW_NUMS, expectAllocationTitle)) {
            return CommonConstant.ErrorMessage.INVALID_ALLOCATION_IMPORT_TEMPLATE;
        }

        int recordNo = 0;
        while (rowIterator.hasNext()) {
            recordNo++;

            Row row = rowIterator.next();

            List<Cell> cellList = new ArrayList<>();

            for (int count = 0; count < CommonConstant.Template.ALLOCATION_IMPORT_TEMPLATE_ROW_NUMS; count++) {
                cellList.add(row.getCell(count, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
            }

            if (cellList.isEmpty() || cellList.size() < 4) {
                break;
            }

            if (Objects.isNull(cellList.get(2)) || cellList.get(2).getStringCellValue() == "") {
                message = message.concat("record no." + recordNo + ": email cell is empty\n");
                continue;
            }

            recordNo = (int) cellList.get(0).getNumericCellValue();

            String employeeName = Objects.isNull(cellList.get(1)) ? "" : cellList.get(1).getStringCellValue();
            String email = cellList.get(2).getStringCellValue();

            if (Objects.isNull(cellList.get(4))) {
                message = message.concat("record no." + recordNo + ": start date cell is empty\n");
                continue;
            }

            if (Objects.isNull(cellList.get(5))) {
                message = message.concat("record no." + recordNo + ": end date cell is empty\n");
                continue;
            }

            Timestamp startDate = getTimeStampFromCellWithFormatddMMyyyy(cellList.get(4));

            Timestamp endDate = getTimeStampFromCellWithFormatddMMyyyy(cellList.get(5));

            if (startDate.after(endDate)) {
                message = message.concat("record no." + recordNo);
                message = message.concat(" - start date: " + startDate.toString().substring(0, 10));
                message = message.concat(" is after");
                message = message.concat(" - end date: " + endDate.toString().substring(0, 10) + "\n");
                continue;
            }

            String projectRole = Objects.isNull(cellList.get(6)) ? "" : cellList.get(6).getStringCellValue();
            float allo = Objects.isNull(cellList.get(7)) ? 0 : (float) cellList.get(7).getNumericCellValue();
            float rate = Objects.isNull(cellList.get(8)) ? 0 : (float) cellList.get(8).getNumericCellValue();
//            String qualified = Objects.isNull(cellList.get(9)) ? "" : cellList.get(9).getStringCellValue();
            String note = Objects.isNull(cellList.get(10)) ? "" : cellList.get(10).getStringCellValue();
            String projectName = Objects.isNull(cellList.get(11)) ? "" : cellList.get(11).getStringCellValue();
            String source = Objects.isNull(cellList.get(12)) ? "" : cellList.get(12).getStringCellValue();
            String allocationType = Objects.isNull(cellList.get(13)) ? "" : cellList.get(13).getStringCellValue();

            User user = userService.findByEmailAndIsDeleted(email, CommonConstant.NOT_DELETED);
            List<Project> projects = projectRepository.findByExactProjectName(projectName.toLowerCase());

            if (Objects.isNull(user)) {
                message = message.concat("record no." + recordNo + ": user- " + employeeName + " not exist\n");
                continue;
            }
            if (projects.isEmpty()) {
                message = message.concat("record no." + recordNo + ": project- " + projectName + " not exist\n");
                continue;
            }
            if (projects.size() > 1) {
                message = message.concat("record no." + recordNo + ": project- " + projectName + " is too poppular to detect\n");
                continue;
            }
            if (allo == 0) {
                message = message.concat("record no." + recordNo + ": allocation = 0\n");
                continue;
            }

            if (!Objects.isNull(startDate) && !Objects.isNull(endDate)
                    && isDuplicateAllocation(user.getId(), projects.get(0).getId(), startDate, endDate)) {
                message = message.concat("record no." + recordNo + ": username: " + employeeName);
                message = message.concat(" - project name: " + projectName);
                message = message.concat(" - start date: " + startDate.toString().substring(0, 10));
                message = message.concat(" - end date: " + endDate.toString().substring(0, 10));
                message = message.concat(" exists\n");
                continue;
            }

            Allocation allocation = new Allocation();
            allocation.setUser(user);
            allocation.setProject(projects.get(0));
            allocation.setOpportunity(null);
            List<Role> roleList = roleRepository.findByRoleCodeOrRoleName(projectRole.toLowerCase());
            if (!roleList.isEmpty()) {
                allocation.setRole(roleList.get(0));
            } else {
                allocation.setRole(roleRepository.findByRoleCodeOrRoleName(CommonConstant.RoleCode.DEVELOPER.toLowerCase()).get(0));
                message = message.concat("record no." + recordNo + ": role with name " + projectRole + " not exists, auto set to developer\n");
                message = message.concat("record no." + recordNo + ": role with name " + projectRole + " not exists\n");
                continue;
            }
            allocation.setAllo(allo);
            allocation.setStartDate(startDate);
            allocation.setEndDate(endDate);
            allocation.setRate(rate);
            allocation.setNote(note);
            allocation.setSource(source);
            allocation.setType(allocationType);
            int loginUserId = userService.getLoginUserId();
            Timestamp timestampNow = new Timestamp(new Date().getTime());
            allocation.setCreatedAt(timestampNow);
            allocation.setCreatedBy(loginUserId);
            allocation.setUpdatedAt(timestampNow);
            allocation.setUpdatedBy(loginUserId);
            allocationRepository.save(allocation);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            ObjectMapper objectMap = new ObjectMapper();
            GoogleUser googleUser = objectMap.convertValue(auth.getPrincipal(), GoogleUser.class);
            User loginUser = userRepository.findByEmailAndIsDeleted(googleUser.getEmail(), CommonConstant.NOT_DELETED);
            saveAllocationDetail(allocation, loginUser);
        }

        return message.isEmpty() ? CommonConstant.ErrorMessage.IMPORT_ALLOCATION_SUCCESS : CommonConstant.ErrorMessage.PREFIX_IMPORT_ERROR + message;
    }

    @Override
    public List<EEResponseDTO> getListEE(EERequestDTO eERequestDTO) {
        List<EEResponseDTO> eeResponseDTOS = new ArrayList<>();
        List<Allocation> allocations = new ArrayList<>();
        if (eERequestDTO.getRoles().isEmpty()) {
            allocations = allocationRepository.getListAllocationByProjectIdAndType(eERequestDTO.getProjectId(), CommonConstant.AllocationType.ACTUAL);

        } else {
            allocations = allocationRepository.getListAllocationByProjectIdAndListRoleIdAndType(eERequestDTO.getProjectId(), eERequestDTO.getRoles(), CommonConstant.AllocationType.ACTUAL);
        }
        for (Allocation allocation : allocations) {
            EEResponseDTO eEResponseDTO = new EEResponseDTO();
            eEResponseDTO.setRoleName(allocation.getRole().getRoleName());
            eEResponseDTO.setEmployeeName(allocation.getUser().getEmployeeName());

            float rate = allocation.getRate();
            eEResponseDTO.setRate(rate);

            Timestamp startDateAllocation = allocation.getStartDate();
            Timestamp endDateAllocation = allocation.getEndDate();

            Timestamp startDateToSearch = Objects.isNull(eERequestDTO.getStartDate()) ? startDateAllocation : eERequestDTO.getStartDate();
            Timestamp endDateToSearch = Objects.isNull(eERequestDTO.getEndDate()) ? endDateAllocation : eERequestDTO.getEndDate();

            Timestamp startDate = startDateToSearch.after(startDateAllocation) ? startDateToSearch : startDateAllocation;
            Timestamp endDate = endDateToSearch.before(endDateAllocation) ? endDateToSearch : endDateAllocation;

            int allocateEE = countDayOfWorkFromDateToDate(startDate, endDate);
            eEResponseDTO.setMdAllocateEE(allocateEE);
            eEResponseDTO.setMdRateEE((float) allocateEE * rate);
            eEResponseDTO.setMmAllocateEE((float) allocateEE / 20);
            eEResponseDTO.setMmRateEE((float) allocateEE * rate / 20);
            eeResponseDTOS.add(eEResponseDTO);
        }
        return eeResponseDTOS;
    }

    @Override
    public List<ObjectValidation> checkConditionToExportEE() {
        List<ObjectValidation> objectValidationList = new ArrayList<>();
        File templateFile = new File(CommonConstant.FilePath.PROJECT_EE_TEMPLATE);
        if (!templateFile.exists()) {
            objectValidationList.add(new ObjectValidation("",
                    String.format(CommonConstant.ErrorMessage.PROJECT_EE_TEMPLATE_NOT_EXIST, CommonConstant.FilePath.PROJECT_EE_TEMPLATE)));
        }

        File exportPath = new File(CommonConstant.FilePath.EXPORT_PATH);
        if (!exportPath.exists()) {
            objectValidationList.add(new ObjectValidation("",
                    String.format(CommonConstant.ErrorMessage.PROJECT_EE_EXPORT_PATH_NOT_EXIST, CommonConstant.FilePath.PROJECT_EE_EXPORT)));
        }
        return objectValidationList;
    }

    @Override
    public String checkConditionToImportAllocation() {
        File importPath = new File(CommonConstant.FilePath.ALLOCATION_IMPORT_PATH);
        if (!importPath.exists()) {
            return String.format(CommonConstant.ErrorMessage.ALLOCATION_IMPORT_PATH_NOT_EXIST,CommonConstant.FilePath.ALLOCATION_IMPORT_PATH);
        }
        return "";
    }

    @Override
    public ByteArrayOutputStream exportEE(EERequestDTO eERequestDTO) throws IOException {
        // make a copy file template  Project-EE-template.xlsx -> open copy
        File projectEETemplate = new File(CommonConstant.FilePath.PROJECT_EE_TEMPLATE);
        File projectEETemplateTemporary = new File(CommonConstant.FilePath.PROJECT_EE_EXPORT);
        Files.copy(projectEETemplate.toPath(), projectEETemplateTemporary.toPath(), StandardCopyOption.REPLACE_EXISTING);

        List<EEResponseDTO> eEExportList = getListEE(eERequestDTO);
        List<EEResponseDTO> eEExportListTemp = new ArrayList<>(eEExportList);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream inputStream = new FileInputStream(projectEETemplateTemporary);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Row titleRow = sheet.getRow(0);
            titleRow.getCell(1).setCellValue(projectRepository.findAllById(eERequestDTO.getProjectId()).getProjectName());

            // export SumEE
            int countAllDataRowSumEEByProject = eEExportList.size();
            if (countAllDataRowSumEEByProject > 1) {
                sheet.shiftRows(CommonConstant.EECalculator.INDEX_OF_HEADER_ROW_OF_EECALCULATOR_BY_PROJECT + 1, sheet.getLastRowNum(), countAllDataRowSumEEByProject - 1,
                        true, true);
            }

            for (int i = 1; i < countAllDataRowSumEEByProject; i++) {
                sheet.copyRows(CommonConstant.Template.PROJECT_EE_INDEX_OF_FIRST_DATA_ROW_OF_PROJECT_EE_CALCULATOR,
                        CommonConstant.Template.PROJECT_EE_INDEX_OF_FIRST_DATA_ROW_OF_PROJECT_EE_CALCULATOR,
                        CommonConstant.Template.PROJECT_EE_INDEX_OF_FIRST_DATA_ROW_OF_PROJECT_EE_CALCULATOR + i, new CellCopyPolicy());
            }

            for (int i = CommonConstant.Template.PROJECT_EE_INDEX_OF_FIRST_DATA_ROW_OF_PROJECT_EE_CALCULATOR;
                 i < CommonConstant.Template.PROJECT_EE_INDEX_OF_FIRST_DATA_ROW_OF_PROJECT_EE_CALCULATOR + countAllDataRowSumEEByProject;
                 i++) {
                Row row = sheet.getRow(i);
                EEResponseDTO eeResponseDTO = eEExportList.get(0);
                row.getCell(0).setCellValue(eeResponseDTO.getRoleName());
                row.getCell(1).setCellValue(eeResponseDTO.getEmployeeName());
                row.getCell(2).setCellValue(eeResponseDTO.getRate());
                row.getCell(3).setCellValue(eeResponseDTO.getMdAllocateEE());
                row.getCell(4).setCellValue(eeResponseDTO.getMdRateEE());
                row.getCell(5).setCellValue(eeResponseDTO.getMmAllocateEE());
                row.getCell(6).setCellValue(eeResponseDTO.getMmRateEE());
                eEExportList.remove(0);
            }

            // export total allocation
            eEExportList = new ArrayList<>(eEExportListTemp);

            double sumMdAllocateEE = 0;
            double sumMdRateEE = 0;
            double sumMmAllocateEE = 0;
            double sumMmRateEE = 0;

            for (EEResponseDTO eeResponseDTO : eEExportList) {
                sumMdAllocateEE += eeResponseDTO.getMdAllocateEE();
                sumMdRateEE += eeResponseDTO.getMdRateEE();
                sumMmAllocateEE += eeResponseDTO.getMmAllocateEE();
                sumMmRateEE += eeResponseDTO.getMmRateEE();
            }

            Row row = sheet.getRow(1);
            row.getCell(3).setCellValue(sumMdAllocateEE);
            row.getCell(4).setCellValue(sumMdRateEE);
            row.getCell(5).setCellValue(sumMmAllocateEE);
            row.getCell(6).setCellValue(sumMmRateEE);

            // calculate SumEE By Role
            Set<String> setRoleName = new HashSet<>();
            for (EEResponseDTO eeResponseDTO : eEExportList) {
                setRoleName.add(eeResponseDTO.getRoleName());
            }

            List<SumEEByRoleResponseDTO> sumEEByRoleResponseDTOS = new ArrayList<>();
            for (String roleName : setRoleName) {
                SumEEByRoleResponseDTO sumEEByRoleResponseDTO = new SumEEByRoleResponseDTO();
                sumEEByRoleResponseDTO.setRoleName(roleName);
                for (EEResponseDTO eeResponseDTO : eEExportList) {
                    if (eeResponseDTO.getRoleName().equalsIgnoreCase(roleName)) {
                        sumEEByRoleResponseDTO.setMdAllocateEE(sumEEByRoleResponseDTO.getMdAllocateEE() + eeResponseDTO.getMdAllocateEE());
                        sumEEByRoleResponseDTO.setMdRateEE(sumEEByRoleResponseDTO.getMdRateEE() + eeResponseDTO.getMdRateEE());
                        sumEEByRoleResponseDTO.setMmAllocateEE(sumEEByRoleResponseDTO.getMmAllocateEE() + eeResponseDTO.getMmAllocateEE());
                        sumEEByRoleResponseDTO.setMmRateEE(sumEEByRoleResponseDTO.getMmRateEE() + eeResponseDTO.getMmRateEE());
                    }
                }
                sumEEByRoleResponseDTOS.add(sumEEByRoleResponseDTO);
            }

            int numberOfRowsOfTableSumEEByRole = sumEEByRoleResponseDTOS.size();

            // export SumEE By Role
            int firstDataRoleOfTableSumEEByRoleIndex = CommonConstant.EECalculator.INDEX_OF_HEADER_ROW_OF_EECALCULATOR_BY_PROJECT +
                    countAllDataRowSumEEByProject +
                    CommonConstant.EECalculator.DISTANCE_BETWEEN_LAST_DATA_ROW_OF_FIRST_TABLE_AND_FIRST_DATA_ROW_OF_SECOND_TABLE;
            for (int i = 1; i < numberOfRowsOfTableSumEEByRole; i++) {
                sheet.copyRows(firstDataRoleOfTableSumEEByRoleIndex,
                        firstDataRoleOfTableSumEEByRoleIndex,
                        firstDataRoleOfTableSumEEByRoleIndex + i, new CellCopyPolicy());
            }


            for (int i = firstDataRoleOfTableSumEEByRoleIndex;
                 i < firstDataRoleOfTableSumEEByRoleIndex + numberOfRowsOfTableSumEEByRole;
                 i++) {
                row = sheet.getRow(i);
                SumEEByRoleResponseDTO sumEEByRoleResponseDTO = sumEEByRoleResponseDTOS.get(0);
                row.getCell(0).setCellValue(sumEEByRoleResponseDTO.getRoleName());
                row.getCell(1).setCellValue(sumEEByRoleResponseDTO.getMdAllocateEE());
                row.getCell(2).setCellValue(sumEEByRoleResponseDTO.getMdRateEE());
                row.getCell(3).setCellValue(sumEEByRoleResponseDTO.getMmAllocateEE());
                row.getCell(4).setCellValue(sumEEByRoleResponseDTO.getMmRateEE());
                sumEEByRoleResponseDTOS.remove(0);
            }

            inputStream.close();
            FileOutputStream fileOutputStream = new FileOutputStream(projectEETemplateTemporary);
            workbook.write(fileOutputStream);
            workbook.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        File fileData = new File(CommonConstant.FilePath.PROJECT_EE_EXPORT);
        FileInputStream inputStream = new FileInputStream(fileData);
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "force-download"));
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ProductTemplate.xlsx");
        workbook.write(stream);
        return stream;
    }

    @Override
    public ByteArrayOutputStream exportAllocationByDay(List<AllocationResponseDTO> allocationResponseDTOS) throws
            IOException {
        Map<String, Integer> columnMap = new HashMap<>(); // map<colTitle, colId>
        // make a copy file template  Project-EE-template.xlsx -> open copy
        File allocationExportByDay = new File(CommonConstant.FilePath.ALLOCATION_BY_DAY_TEMPLATE);
        File allocationExportByDayTemporary = new File(CommonConstant.FilePath.ALLOCATION_BY_DAY_EXPORT);
        Files.copy(allocationExportByDay.toPath(), allocationExportByDayTemporary.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // get minStartDate & maxEndDate to generate column
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");


        Timestamp startDate = allocationResponseDTOS.stream().min(comparing(AllocationResponseDTO::getStartDate)).get().getStartDate();
        Timestamp endDate = allocationResponseDTOS.stream().max(comparing(AllocationResponseDTO::getEndDate)).get().getEndDate();

        ArrayList<String> timeBetweenTwoDatesArrLst = getListDateTimeBetweenTwoDates(startDate, endDate);// gen date tu start date to end date
        // get minStartDate & maxEndDate to generate column
        ArrayList<String> timeBetweenTwoDatesArrLstClone = new ArrayList<>();
        timeBetweenTwoDatesArrLstClone.addAll(timeBetweenTwoDatesArrLst);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream inputStream = new FileInputStream(allocationExportByDayTemporary);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Row titleRow = sheet.getRow(0);
            int numberOfCellToAdd = timeBetweenTwoDatesArrLst.size();
            for (int i = 0; i < numberOfCellToAdd; i++) {
                titleRow.createCell(6 + i);

                String date = timeBetweenTwoDatesArrLst.get(0);
                columnMap.put(date, 6 + i);
                titleRow.getCell(6 + i).setCellValue(date);
                timeBetweenTwoDatesArrLst.remove(0);
            }

            for (int i = 1; i < 1 + allocationResponseDTOS.size(); i++) {
                Row row = sheet.createRow(i);
                AllocationResponseDTO allocationResponseDTO = allocationResponseDTOS.get(i - 1);
                for (int k = 0; k < 6 + numberOfCellToAdd; k++) {
                    row.createCell(k);
                }
                row.getCell(0).setCellValue(userRepository.getEmployeeCodeById(allocationResponseDTO.getEmployeeId()));
                row.getCell(1).setCellValue(allocationResponseDTO.getEmployeeName());
                row.getCell(2).setCellValue(allocationResponseDTO.getProjectName());
                row.getCell(3).setCellValue(allocationResponseDTO.getRoleName());
                row.getCell(4).setCellValue(allocationResponseDTO.getRate());
                row.getCell(5).setCellValue("h");
                //tu allocation start date - end date -> fill data vao bang

                String alloStartDate = timeStampToString(allocationResponseDTO.getStartDate());
                String alloEndDate = timeStampToString(allocationResponseDTO.getEndDate());

                int alloStartDateColId = columnMap.get(alloStartDate); // map<colTitle, colId>
                int alloEndDateColId = columnMap.get(alloEndDate);
                for (int m = alloStartDateColId; m <= alloEndDateColId; m++) {
                    row.getCell(m).setCellValue(new DecimalFormat("###.##")
                            .format(CommonConstant.AllocationExport.TOTAL_WORKING_HOURS_OF_A_DAY * allocationResponseDTO.getAllo() / 100));
                }
            }

            inputStream.close();

            FileOutputStream fileOutputStream = new FileOutputStream(allocationExportByDayTemporary);
            workbook.write(fileOutputStream);
            workbook.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        File fileData = new File(CommonConstant.FilePath.ALLOCATION_BY_DAY_EXPORT);

        FileInputStream inputStream = new FileInputStream(fileData);

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "force-download"));
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Allocation-By-Day-Export.xlsx");
        workbook.write(stream);
        return stream;
    }

    @Override
    public ByteArrayOutputStream exportAllocationByMonth(List<AllocationResponseDTO> allocationResponseDTOS) throws
            IOException {
        Map<String, Integer> columnMap = new HashMap<>();
        File allocationExportByMonth = new File(CommonConstant.FilePath.ALLOCATION_BY_MONTH_TEMPLATE);
        File allocationExportByMonthTemporary = new File(CommonConstant.FilePath.ALLOCATION_BY_MONTH_EXPORT);
        Files.copy(allocationExportByMonth.toPath(), allocationExportByMonthTemporary.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // get minStartDate & maxEndDate to generate column
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

        Timestamp startDate = allocationResponseDTOS.stream().min(comparing(AllocationResponseDTO::getStartDate)).get().getStartDate();
        Timestamp endDate = allocationResponseDTOS.stream().max(comparing(AllocationResponseDTO::getEndDate)).get().getEndDate();

        ArrayList<String> monthBetweenTwoDatesArrLst = getListMonthBetweenTwoDates(startDate, endDate);// generate month from start date to end date
        // get minStartDate & maxEndDate to generate column
        ArrayList<String> monthBetweenTwoDatesArrLstClone = new ArrayList<>();
        monthBetweenTwoDatesArrLstClone.addAll(monthBetweenTwoDatesArrLst);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream inputStream = new FileInputStream(allocationExportByMonthTemporary);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Row titleRow = sheet.getRow(0);
            int numberOfCellToAdd = monthBetweenTwoDatesArrLst.size();
            for (int i = 0; i < numberOfCellToAdd; i++) {
                titleRow.createCell(7 + i);

                String date = monthBetweenTwoDatesArrLst.get(0);
                columnMap.put(date, 7 + i);
                titleRow.getCell(7 + i).setCellValue(date);
                monthBetweenTwoDatesArrLst.remove(0);
            }

            for (int i = 1; i < 1 + allocationResponseDTOS.size(); i++) {
                Row row = sheet.createRow(i);
                AllocationResponseDTO allocationResponseDTO = allocationResponseDTOS.get(i - 1);
                for (int k = 0; k < 7 + numberOfCellToAdd; k++) {
                    row.createCell(k);
                }
                row.getCell(0).setCellValue(allocationResponseDTO.getType());
                row.getCell(1).setCellValue(allocationResponseDTO.getProjectName());
                row.getCell(2).setCellValue(userRepository.getEmployeeCodeById(allocationResponseDTO.getEmployeeId()));
                row.getCell(3).setCellValue(allocationResponseDTO.getRoleName());
                row.getCell(4).setCellValue(allocationResponseDTO.getEmployeeName());
                row.getCell(5).setCellValue((new DecimalFormat("###.##")
                        .format(allocationResponseDTO.getRate())));
                row.getCell(6).setCellValue("%");
                //tu allocation start date - end date -> fill data vao bang

                String alloStartDate = timeStampToStringMyyyy(allocationResponseDTO.getStartDate());
                String alloEndDate = timeStampToStringMyyyy(allocationResponseDTO.getEndDate());

                int alloStartDateColId = columnMap.get(alloStartDate); // map<colTitle, colId>
                int alloEndDateColId = columnMap.get(alloEndDate);
                for (int m = alloStartDateColId; m <= alloEndDateColId; m++) {
                    System.out.println("alloStartDate: " + alloStartDate);
                    System.out.println("alloStartDateColId: " + alloStartDateColId);
                    System.out.println("alloEndDate: " + alloEndDate);
                    System.out.println("alloEndDateColId: " + alloEndDateColId);
                    row.getCell(m).setCellValue(allocationResponseDTO.getAllo());
                }
            }

            inputStream.close();

            FileOutputStream fileOutputStream = new FileOutputStream(allocationExportByMonthTemporary);
            workbook.write(fileOutputStream);
            workbook.close();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        File fileData = new File(CommonConstant.FilePath.ALLOCATION_BY_MONTH_EXPORT);

        FileInputStream inputStream = new FileInputStream(fileData);

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "force-download"));
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Allocation-By-Month-Export.xlsx");
        workbook.write(stream);
        return stream;
    }

    @Override
    public ByteArrayOutputStream exportAllocationByPeriod(List<AllocationResponseDTO> allocationResponseDTOS) throws IOException {
        Map<String, Integer> columnMap = new HashMap<>(); //map<colTitle, colId>
        File allocationExportByPeriod = new File(CommonConstant.FilePath.ALLOCATION_BY_PERIOD_TEMPLATE);
        File allocationExportByPeriodTemporary = new File(CommonConstant.FilePath.ALLOCATION_BY_PERIOD_EXPORT);
        Files.copy(allocationExportByPeriod.toPath(), allocationExportByPeriodTemporary.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // get minStartDate & maxEndDate to generate column
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

        Timestamp startDate = allocationResponseDTOS.stream().min(comparing(AllocationResponseDTO::getStartDate)).get().getStartDate();
        Timestamp endDate = allocationResponseDTOS.stream().max(comparing(AllocationResponseDTO::getEndDate)).get().getEndDate();

        ArrayList<String> periodBetweenTwoDatesArrLst = getListPeriodBetweenTwoDates(startDate, endDate);
        ArrayList<String> periodBetweenTwoDatesArrLstClone = new ArrayList<>();
        periodBetweenTwoDatesArrLstClone.addAll(periodBetweenTwoDatesArrLst);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream inputStream = new FileInputStream(allocationExportByPeriodTemporary);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Row titleRow = sheet.getRow(0);
            int numberOfCellToAdd = periodBetweenTwoDatesArrLst.size();
            for (int i = 0; i < numberOfCellToAdd; i++) {
                titleRow.createCell(4 + i);
                String date = periodBetweenTwoDatesArrLst.get(0);
                columnMap.put(date, 4 + i);
                titleRow.getCell(4 + i).setCellValue(date);
                periodBetweenTwoDatesArrLst.remove(0);
            }

            for (int i = 1; i < 1 + allocationResponseDTOS.size(); i++) {
                Row row = sheet.createRow(i);
                AllocationResponseDTO allocationResponseDTO = allocationResponseDTOS.get(i - 1);
                for (int k = 0; k < 4 + numberOfCellToAdd; k++) {
                    row.createCell(k);
                }
                row.getCell(0).setCellValue(userRepository.getEmployeeCodeById(allocationResponseDTO.getEmployeeId()));
                row.getCell(1).setCellValue(allocationResponseDTO.getEmployeeName());
                row.getCell(2).setCellValue(projectRepository.findAllById(allocationResponseDTO.getProjectId()).getProjectName());
                row.getCell(3).setCellValue(allocationResponseDTO.getDepartment());

                Timestamp startDateAllocation = (Timestamp) allocationResponseDTO.getStartDate().clone();
                Calendar calStartDateAllocation = Calendar.getInstance();
                if ((Calendar.SATURDAY != calStartDateAllocation.get(Calendar.DAY_OF_WEEK))
                        && (Calendar.SUNDAY != calStartDateAllocation.get(Calendar.DAY_OF_WEEK))) {
                    plusNumberOfDayToTimeStamp(startDateAllocation, 2);
                }

                String weekOfStartDate = getCurrentWeekDayOfTimeStamp(startDateAllocation);
                String weekOfEndDate = getCurrentWeekDayOfTimeStamp(allocationResponseDTO.getEndDate());

                int weekOfStartDateCellIndex = columnMap.get(weekOfStartDate);
                int weekOfEndDateCellIndex = columnMap.get(weekOfEndDate);

                for (int m = weekOfStartDateCellIndex; m <= weekOfEndDateCellIndex; m++) {
                    row.getCell(m).setCellValue(allocationResponseDTO.getAllo());
                }
            }
            inputStream.close();

            FileOutputStream fileOutputStream = new FileOutputStream(allocationExportByPeriodTemporary);
            workbook.write(fileOutputStream);
            workbook.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        File fileData = new File(CommonConstant.FilePath.ALLOCATION_BY_PERIOD_EXPORT);

        FileInputStream inputStream = new FileInputStream(fileData);

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "force-download"));
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Allocation-By-Period-Export.xlsx");
        workbook.write(stream);
        return stream;
    }

    private ArrayList<String> getListDateTimeBetweenTwoDates(Timestamp minStartDate, Timestamp maxEndDate) {
        ArrayList<String> allDatesStringArrLst = new ArrayList<>();
        DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");

        Timestamp minStartDateClone = new Timestamp(minStartDate.getTime());
        Timestamp maxStartDateClone = plus1DayToTimeStamp(new Timestamp(maxEndDate.getTime()));

        while (minStartDateClone.before(maxStartDateClone)) {
            allDatesStringArrLst.add(df1.format(minStartDateClone));
            plus1DayToTimeStamp(minStartDateClone);
        }
        return allDatesStringArrLst;

    }

    private ArrayList<String> getListMonthBetweenTwoDates(Timestamp minStartDate, Timestamp maxEndDate) {
        ArrayList<String> allDatesStringArrLst = new ArrayList<>();
        DateFormat formater = new SimpleDateFormat("M/yyyy");

        Calendar beginCalendar = Calendar.getInstance();
        Calendar finishCalendar = Calendar.getInstance();

        beginCalendar.setTime(new Date(minStartDate.getTime()));
        finishCalendar.setTime(new Date(maxEndDate.getTime()));
        beginCalendar.set(Calendar.DAY_OF_MONTH, 1);
        finishCalendar.set(Calendar.DAY_OF_MONTH, 20);

        while (beginCalendar.before(finishCalendar)) {
            allDatesStringArrLst.add(formater.format(beginCalendar.getTime()));
            beginCalendar.add(Calendar.MONTH, 1);
        }
        return allDatesStringArrLst;

    }

    private ArrayList<String> getListPeriodBetweenTwoDates(Timestamp startDate, Timestamp endDate) {
        ArrayList<String> allDatesStringArrLst = new ArrayList<>();

        Calendar beginCalendar = Calendar.getInstance();
        Calendar finishCalendar = Calendar.getInstance();

        beginCalendar.setTime(new Date(startDate.getTime()));
        finishCalendar.setTime(new Date(endDate.getTime()));

        while (beginCalendar.before(finishCalendar)) {
            allDatesStringArrLst.add(getCurrentWeekDayOfCalendar(beginCalendar));
            beginCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
            beginCalendar.add(Calendar.DATE, 1);
        }
        for (String s : allDatesStringArrLst) {
            System.out.println(s);
        }
        return allDatesStringArrLst;

    }

    private static Timestamp plus1DayToTimeStamp(Timestamp timestamp) {
        Timestamp timestampnew = timestamp;
        if (timestampnew != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(timestampnew);
            cal.add(Calendar.DAY_OF_WEEK, 1);
            timestampnew.setTime(cal.getTime().getTime());
            timestampnew = new Timestamp(cal.getTime().getTime());
            return timestampnew;
        }
        return timestampnew;
    }

    private static Timestamp plusNumberOfDayToTimeStamp(Timestamp timestamp, int numberOfDay) {
        Timestamp timestampnew = timestamp;
        if (timestampnew != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(timestampnew);
            cal.add(Calendar.DAY_OF_WEEK, numberOfDay);
            timestampnew.setTime(cal.getTime().getTime());
            timestampnew = new Timestamp(cal.getTime().getTime());
            return timestampnew;
        }
        return timestampnew;
    }

    private String timeStampToString(Timestamp timestamp) {
        Date date = new Date();
        date.setTime(timestamp.getTime());
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    private String timeStampToStringMyyyy(Timestamp timestamp) {
        Date date = new Date();
        date.setTime(timestamp.getTime());
        return new SimpleDateFormat("M/yyyy").format(date);
    }

    private String getCurrentWeekDayOfCalendar(Calendar calendar) {
        String str = "";
        Calendar calendar1 = (Calendar) calendar.clone();
        calendar1.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        str = str.concat(new SimpleDateFormat("dd/MM/yyyy").format(calendar1.getTime()));
        str = str.concat(" - ");
        calendar1.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        str = str.concat(new SimpleDateFormat("dd/MM/yyyy").format(calendar1.getTime()));
        return str;
    }

    private Timestamp getTimeStampFromCellWithFormatddMMyyyy(Cell cell) {
        try {
            String cellString = cell.getStringCellValue();
            if (!cellString.isEmpty()) {
                return new Timestamp(getDateddMMyyyyFromString(cellString).getTime());
            } else return null;
        } catch (IllegalStateException | ParseException illegalStateException) {
            return new Timestamp(cell.getDateCellValue().getTime());
        }
    }

    private Date getDateFromCellWithFormatMyyyy(Cell cell) {
        try {
            return getDateMyyyyFromString(cell.getStringCellValue());
        } catch (IllegalStateException | ParseException illegalStateException) {
            return cell.getDateCellValue();
        }
    }
    private Date getDateFromCell(Cell cell) {
        try {
            return getDateddMMyyyyFromString(cell.getStringCellValue());
        } catch (IllegalStateException | ParseException illegalStateException) {
            return cell.getDateCellValue();
        }
    }

    private boolean validateTitleRow(Row row, int numberOfTitleElement, List<String> expectTitles) {
        List<String> actualTitles = new ArrayList<>();
        for (int i = 0; i < numberOfTitleElement; i++) {
            actualTitles.add(row.getCell(i).getStringCellValue());
        }
        return actualTitles.equals(expectTitles);
    }

    private String convertNameMonthToNumberMonth(String month) {
        month = month.replace("Jan", "1");
        month = month.replace("Feb", "2");
        month = month.replace("Mar", "3");
        month = month.replace("Apr", "4");
        month = month.replace("May", "5");
        month = month.replace("Jun", "6");
        month = month.replace("Jul", "7");
        month = month.replace("Aug", "8");
        month = month.replace("Sep", "9");
        month = month.replace("Oct", "10");
        month = month.replace("Nov", "11");
        month = month.replace("Dec", "12");
        month = month.replace("-", "/");
        return month;
    }

    private String getCurrentWeekDayOfTimeStamp(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp.getTime()));
        return getCurrentWeekDayOfCalendar(calendar);
    }

    private boolean equalAllocation(String current, String previous) {
        return current.equals(previous);
    }

    private Timestamp getDayAfterATimestamp(Timestamp timestamp, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());
        cal.add(Calendar.DATE, amount);
        return new Timestamp(cal.getTimeInMillis());
    }

    private void saveAllocationDetail(Allocation allocation, User loginUser) {
        AllocationDetail allocationDetail = new AllocationDetail();
        allocationDetail.setAllocation(allocation);
        allocationDetail.setAllo(allocation.getAllo());
        allocationDetail.setUser(allocation.getUser());
        allocationDetail.setProject(allocation.getProject());
        allocationDetail.setOpportunity(allocation.getOpportunity());
        allocationDetail.setRole(allocation.getRole());
        allocationDetail.setCreatedBy(loginUser.getId());
        allocationDetail.setUpdatedBy(loginUser.getId());
        allocationDetailService.deleteByAllocationId(allocation.getId());
        allocationDetailService.saveAllocationDetail(allocationDetail);
    }

    public Allocation setAllocation(String performance, String allocationCurrent, User user, String
            alloType, List<Role> roles, Project project, Timestamp startDate,
                                    Timestamp endDate, float performanceRate) {
        Allocation allocation = new Allocation();
        float allo = Float.parseFloat(allocationCurrent);
        if (performance.equalsIgnoreCase(CommonConstant.AllocationUnit.HOUR)) {
            allo = allo * CommonConstant.AllocationUnit.PERCENT / CommonConstant.AllocationUnit.TOTAL_HOUR;
        } else {
            if (performance.equalsIgnoreCase(CommonConstant.AllocationUnit.MM)) {
                allo = allo * CommonConstant.AllocationUnit.PERCENT;
            }
        }
        allocation.setAllo(allo);
        allocation.setUser(user);
        allocation.setType(alloType);
        allocation.setProject(project);
        allocation.setRole(roles.get(0));
        allocation.setStartDate(startDate);
        allocation.setEndDate(endDate);
        allocation.setRate(performanceRate);
        int loginUserId = userService.getLoginUserId();
        Timestamp timestampNow = new Timestamp(new Date().getTime());
        allocation.setCreatedAt(timestampNow);
        allocation.setCreatedBy(loginUserId);
        allocation.setUpdatedAt(timestampNow);
        allocation.setUpdatedBy(loginUserId);
        return allocation;
    }

    private boolean checkConsecutiveMonth(Calendar current, Calendar previous) {
        Calendar calendar = (Calendar) previous.clone();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.add(Calendar.MONTH, 1);
        return current.equals(calendar);
    }

    private Integer countDayOfWorkFromDateToDate(Timestamp startDate, Timestamp endDate) {
        Calendar calStartDate = Calendar.getInstance();
        Calendar calEndDate = Calendar.getInstance();

        calStartDate.setTime(startDate);
        calEndDate.setTime(endDate);
        int numberOfDays = 0;
        while (calStartDate.before(calEndDate)) {
            if ((Calendar.SATURDAY != calStartDate.get(Calendar.DAY_OF_WEEK))
                    && (Calendar.SUNDAY != calStartDate.get(Calendar.DAY_OF_WEEK)) && !isHolidayDay(calStartDate)) {
                numberOfDays++;
            }
            calStartDate.add(Calendar.DATE, 1);
        }
        return numberOfDays;
    }

    private boolean isHolidayDay(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return allocationRepository.isHolidayDay(year, month, day);
    }

    private Date getDateddMMyyyyFromString(String timeStampString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy");
        if (timeStampString.charAt(timeStampString.length() - 4) == '/') {
            return dateFormat.parse(timeStampString);
        }
        if (timeStampString.charAt(timeStampString.length() - 4) == '-') {
            if (timeStampString.charAt(timeStampString.length() - 5) == '/') {
                return dateFormat.parse(timeStampString);
            }
            if (timeStampString.charAt(timeStampString.length() - 5) == '-') {
                return dateFormat1.parse(timeStampString);
            }
        }
        return new Date();
    }

    private Date getDateMyyyyFromString(String timeStampString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/yyyy");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("M-yyyy");
        if (timeStampString.charAt(timeStampString.length() - 4) == '/') {
            return dateFormat.parse(timeStampString);
        }
        if (timeStampString.charAt(timeStampString.length() - 4) == '-') {
            return dateFormat1.parse(timeStampString);
        }
        return new Date();
    }

    private Allocation toAllocation(AllocationRequestDTO allocationRequestDTO) {
        Allocation allocationOldData = allocationRepository.findAllocationById(allocationRequestDTO.getId());
        Allocation allocation = modelMapper.map(allocationRequestDTO, Allocation.class);
        User user = userRepository.findAllById(allocationRequestDTO.getEmployeeId());
        Project project = projectRepository.findAllById(allocationRequestDTO.getProjectId());
        Opportunity opportunity = opportunityRepository.findAllById(allocationRequestDTO.getOppId());
        Role role = roleRepository.findAllById(allocationRequestDTO.getRoleId());

        allocation.setId(allocationRequestDTO.getId());
        allocation.setUser(user);
        allocation.setProject(project);
        allocation.setOpportunity(opportunity);
        allocation.setRole(role);
        allocation.setType(allocationRequestDTO.getType());

        int loginUserId = userService.getLoginUserId();
        if (allocationRequestDTO.getId() == 0) {
            allocation.setCreatedBy(loginUserId);
        } else {
            allocation.setCreatedBy(allocationOldData.getCreatedBy());
        }
        allocation.setUpdatedBy(loginUserId);
        return allocation;
    }

    private AllocationByPeriod getStartDateAndEndDateForAllocationPeriodFromString(AllocationByPeriod allocationByPeriod,
                                                                                   String startEndDateString) throws ParseException {
        String[] splited = startEndDateString.split("\\s+");
        Timestamp startDate = getTimeStampMdyyyyFromString(splited[0]);
        Timestamp endDate = getTimeStampMdyyyyFromString(splited[2]);
        allocationByPeriod.setAlloStartDate(startDate);
        allocationByPeriod.setAlloEndDate(plusNumberOfDayToTimeStamp(endDate, 2));
        return allocationByPeriod;
    }

    private Timestamp getTimeStampMdyyyyFromString(String timeStampString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
        return new java.sql.Timestamp(dateFormat.parse(timeStampString).getTime());
    }

    public void setDate(Calendar calendarCurrent) {
        calendarCurrent.set(Calendar.DAY_OF_MONTH, calendarCurrent.getActualMinimum(Calendar.DAY_OF_MONTH));
        startDate = new Timestamp(calendarCurrent.getTimeInMillis());
        calendarCurrent.set(Calendar.DAY_OF_MONTH, calendarCurrent.getActualMaximum(Calendar.DAY_OF_MONTH));
        endDate = new Timestamp(calendarCurrent.getTimeInMillis());
    }
}