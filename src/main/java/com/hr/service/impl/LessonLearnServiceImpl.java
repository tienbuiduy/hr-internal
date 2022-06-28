package com.hr.service.impl;

import com.hr.constant.CommonConstant;
import com.hr.model.LessonLearn;
import com.hr.model.Project;
import com.hr.model.dto.request.AllocationRequestDTO;
import com.hr.model.dto.request.LessonLearnSearchRequestDTO;
import com.hr.model.dto.response.LessonLearnResponseDTO;
import com.hr.repository.LessonLearnRepository;
import com.hr.repository.ProjectRepository;
import com.hr.service.LessonLearnService;
import com.hr.service.UserService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional(rollbackOn = Exception.class)
public class LessonLearnServiceImpl implements LessonLearnService {
    List<String> expectLessonLearnTitles = Arrays.asList(
            "#",
            "Date",
            "Description",
            "Category",
            "Impact",
            "Root cause",
            "Corrective action/\nPrevention action",
            "PIC",
            "Deadline",
            "Expected result",
            "Actual result",
            "Status",
            "Opinion by Division's PMO",
            "Other notes",
            "Nguồn gốc vấn đề",
            "Long term solution");

    private ModelMapper modelMapper;

    private LessonLearnRepository lessonLearnRepository;

    private ProjectRepository projectRepository;

    private UserService userService;

    public LessonLearnServiceImpl(LessonLearnRepository lessonLearnRepository, ProjectRepository projectRepository,
                                  UserService userService, ModelMapper modelMapper) {
        this.lessonLearnRepository = lessonLearnRepository;
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<LessonLearnResponseDTO> list() {
        return lessonLearnRepository.getListLessonLearn().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public void save(AllocationRequestDTO allocationRequestDTO) {

    }

    @Override
    public List<LessonLearnResponseDTO> search(LessonLearnSearchRequestDTO lessonLearnSearchRequestDTO) {
        List<LessonLearn> lessonLearns = lessonLearnRepository.search( lessonLearnSearchRequestDTO.getDescription().toLowerCase(),
                lessonLearnSearchRequestDTO.getCategory().toLowerCase(),
                lessonLearnSearchRequestDTO.getImpact().toLowerCase(),
                lessonLearnSearchRequestDTO.getRootCause().toLowerCase(),
                lessonLearnSearchRequestDTO.getCorrectiveAction().toLowerCase(),
                lessonLearnSearchRequestDTO.getExpectResult().toLowerCase(),
                lessonLearnSearchRequestDTO.getOtherNotes().toLowerCase(),
                lessonLearnSearchRequestDTO.getProblemSource().toLowerCase(),
                lessonLearnSearchRequestDTO.getLongTermSolution().toLowerCase());
        return lessonLearns.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public String importLessonLearn(MultipartFile multipartFile) throws IOException {
        String message = "";

        String uploadDir = CommonConstant.FilePath.LESSON_LEARN_IMPORT_PATH;

        Path copyLocation;
        copyLocation = Paths
                .get(uploadDir + File.separator + StringUtils.cleanPath(multipartFile.getOriginalFilename()));
        Files.copy(multipartFile.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
        File fileData = new File(copyLocation.toString());

        FileInputStream inputStream = new FileInputStream(fileData);

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

        XSSFSheet sheet = null;
        Iterator<Sheet> sheetIterator = workbook.sheetIterator();

        while (sheetIterator.hasNext()) {
            Sheet sheetOfWorkbook = sheetIterator.next();
            if(sheetOfWorkbook.getSheetName().equals("Kaizen log")){
                sheet = (XSSFSheet)sheetOfWorkbook;
                break;
            }
        }
        if(Objects.isNull(sheet)){
            sheet = workbook.getSheetAt(0);
        }
        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next();
        rowIterator.next();

        Row projectNameRow = rowIterator.next();
        Iterator<Cell> cellIteratorOfProjectNameRow = projectNameRow.cellIterator();
        cellIteratorOfProjectNameRow.next();
        cellIteratorOfProjectNameRow.next();
        String projectName = cellIteratorOfProjectNameRow.next().getStringCellValue();
        List<Project> projects = projectRepository.findByExactProjectName(projectName.toLowerCase());
        if(projects.isEmpty()){
            message = message.concat("Project with name " + projectName + " is not exists" );
            return message;
        }
        if(projects.size() > 1){
            message = message.concat("Project with name " + projectName + " is too popular to detect" );
            return message;
        }

        Row divisionNameRow = rowIterator.next();

        List<Cell> listCellOfDivsionRow = getCellListFromRow(divisionNameRow);

        Cell divisionCell = listCellOfDivsionRow.get(2);
        String divisionName = divisionCell.getStringCellValue().isEmpty()? "NS3" : divisionCell.getStringCellValue();
        rowIterator.next();
        Row titleRow = rowIterator.next();

        if(!validateTitleRow(titleRow)){
            return CommonConstant.ErrorMessage.INVALID_LESSON_LEARN_IMPORT_TEMPLATE;
        }

        rowIterator.next();

        int loginUserId = userService.getLoginUserId();

        int recordNo = 0;
        while (rowIterator.hasNext()) {
            recordNo++;
            Row row = rowIterator.next();

            List<Cell> cellList = new ArrayList<>();

            for( int count = 0; count < CommonConstant.Template.LESSON_LEARN_IMPORT_TEMPLATE_ROW_NUMS; count++){
                cellList.add(row.getCell(count, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
            }
            if(cellList.isEmpty() || cellList.size() < 4 || cellList.get(2).getStringCellValue().concat(cellList.get(5).getStringCellValue()).isEmpty()){
                break;
            }

            if(Objects.isNull(cellList.get(2)) || cellList.get(2).getStringCellValue() == ""){
                message = message.concat("record no." + recordNo + ": Description cell is empty\n");
                continue;
            }

            if(Objects.isNull(cellList.get(5)) || cellList.get(5).getStringCellValue() == ""){
                message = message.concat("record no." + recordNo + ": Root cause cell is empty\n");
                continue;
            }

            if(Objects.isNull(cellList.get(6)) || cellList.get(6).getStringCellValue() == ""){
                message = message.concat("record no." + recordNo + ": Corrective action/ Prevention action cell is empty\n");
                continue;
            }

//            int allocationNo = (int) cellList.get(0).getNumericCellValue();
            Timestamp loggingDate = getTimeStampFromCell(cellList.get(1));
            String description = cellList.get(2).getStringCellValue();
            String category = Objects.isNull(cellList.get(3)) ? "" : cellList.get(3).getStringCellValue();
            String impact = Objects.isNull(cellList.get(4)) ? "" : cellList.get(4).getStringCellValue();
            String rootCause = cellList.get(5).getStringCellValue();
            String correctiveAction = cellList.get(6).getStringCellValue();
            String pic = Objects.isNull(cellList.get(7)) ? "" : cellList.get(7).getStringCellValue();
            Timestamp deadLine = getTimeStampFromCell(cellList.get(8));
            String expectResult = Objects.isNull(cellList.get(9)) ? "" : cellList.get(9).getStringCellValue();
            String actualResult = Objects.isNull(cellList.get(10)) ? "" : cellList.get(10).getStringCellValue();
            String status = Objects.isNull(cellList.get(11)) ? "" : cellList.get(11).getStringCellValue();
            String opinion = Objects.isNull(cellList.get(12)) ? "" : cellList.get(12).getStringCellValue();
            String otherNotes = Objects.isNull(cellList.get(13)) ? "" : cellList.get(13).getStringCellValue();
            String problemSource = Objects.isNull(cellList.get(14)) ? "" : cellList.get(14).getStringCellValue();
            String longTermSolution = Objects.isNull(cellList.get(15)) ? "" : cellList.get(15).getStringCellValue();


            List<LessonLearn> listLessonLearnExist = lessonLearnRepository.findLessonLearnExists(description.toLowerCase(),
                    rootCause.toLowerCase(), correctiveAction.toLowerCase(), projectName.toLowerCase());
            if(!listLessonLearnExist.isEmpty()){
                LessonLearn lessonLearnExist = listLessonLearnExist.get(0);
                lessonLearnExist.setDate(loggingDate);
                lessonLearnExist.setCategory(category);
                lessonLearnExist.setImpact(impact);
                lessonLearnExist.setPic(pic);
                lessonLearnExist.setDeadLine(deadLine);
                lessonLearnExist.setExpectResult(expectResult);
                lessonLearnExist.setActualResult(actualResult);
                lessonLearnExist.setStatus(status);
                lessonLearnExist.setOpinion(opinion);
                lessonLearnExist.setOtherNotes(otherNotes);
                lessonLearnExist.setProblemSource(problemSource);
                lessonLearnExist.setLongTermSolution(longTermSolution);
                lessonLearnExist.setDivision(divisionName);
                lessonLearnExist.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                lessonLearnExist.setUpdatedBy(loginUserId);
                lessonLearnRepository.save(lessonLearnExist);
            } else {
                LessonLearn lessonLearn = new LessonLearn();
                lessonLearn.setDate(loggingDate);
                lessonLearn.setDescription(description);
                lessonLearn.setCategory(category);
                lessonLearn.setImpact(impact);
                lessonLearn.setRootCause(rootCause);
                lessonLearn.setCorrectiveAction(correctiveAction);
                lessonLearn.setPic(pic);
                lessonLearn.setDeadLine(deadLine);
                lessonLearn.setExpectResult(expectResult);
                lessonLearn.setActualResult(actualResult);
                lessonLearn.setStatus(status);
                lessonLearn.setOpinion(opinion);
                lessonLearn.setOtherNotes(otherNotes);
                lessonLearn.setProblemSource(problemSource);
                lessonLearn.setLongTermSolution(longTermSolution);
                lessonLearn.setProjectName(projectName);
                lessonLearn.setProjectId(projects.get(0).getId());
                lessonLearn.setDivision(divisionName);
                lessonLearn.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                lessonLearn.setCreatedBy(loginUserId);
                lessonLearn.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                lessonLearn.setUpdatedBy(loginUserId);
                lessonLearnRepository.save(lessonLearn);
            }
        }

        if(message.isEmpty()){
            return CommonConstant.ErrorMessage.IMPORT_LESSON_LEARN_SUCCESS;
        }
        return CommonConstant.ErrorMessage.PREFIX_IMPORT_ERROR + message;
    }

    private LessonLearnResponseDTO toResponseDTO(LessonLearn lessonLearn) {
        LessonLearnResponseDTO lessonLearnResponseDTO = modelMapper.map(lessonLearn, LessonLearnResponseDTO.class);
        return lessonLearnResponseDTO;
    }

    @Override
    public void delete(Integer id, int loginUserId) {
        Timestamp timestampNow = new Timestamp(System.currentTimeMillis());
        lessonLearnRepository.deleteLessonLearn(id, loginUserId, timestampNow);
    }

    @Override
    public String checkConditionToImportLessonLearn() {
        File importPath = new File(CommonConstant.FilePath.LESSON_LEARN_IMPORT_PATH);
        if(!importPath.exists()){
            return String.format(CommonConstant.ErrorMessage.LESSON_LEARN_IMPORT_PATH_NOT_EXIST, CommonConstant.FilePath.LESSON_LEARN_IMPORT_PATH);
        }
        return "";
    }

    public Timestamp getTimeStampFromString(String timeStampString){
        Timestamp timestamp = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormat4 = new SimpleDateFormat("d-M-yyyy");
            SimpleDateFormat dateFormat5 = new SimpleDateFormat("d/M/yyyy");

            Date parsedDate = null;

            if(timeStampString.charAt(2) == '/'){
                parsedDate = dateFormat.parse(timeStampString);
            }
            if(timeStampString.charAt(2) == '-'){
                parsedDate = dateFormat1.parse(timeStampString);
            }
            if(timeStampString.charAt(4) == '/'){
                parsedDate = dateFormat2.parse(timeStampString);
            }
            if(timeStampString.charAt(4) == '-'){
                parsedDate = dateFormat3.parse(timeStampString);
            }

            if(timeStampString.charAt(1) == '-'){
                parsedDate = dateFormat4.parse(timeStampString);
            }
            if(timeStampString.charAt(1) == '/'){
                parsedDate = dateFormat4.parse(timeStampString);
            }

            timestamp = new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    private boolean validateTitleRow(Row row){
        List<String> actualTitles = new ArrayList<>();
        for (int i = 0; i < CommonConstant.Template.LESSON_LEARN_IMPORT_TEMPLATE_ROW_NUMS; i++){
            actualTitles.add(row.getCell(i).getStringCellValue());
        }
        return actualTitles.equals(expectLessonLearnTitles);
    }

    private Timestamp getTimeStampFromCell(Cell cell) {
        try {
            String cellString = cell.getStringCellValue();
            if (!cellString.isEmpty()) {
                return new Timestamp(getDateFromString(cellString).getTime());
            } else return null;
        } catch (IllegalStateException | ParseException illegalStateException) {
            return new Timestamp(cell.getDateCellValue().getTime());
        }
    }

    private Date getDateFromString(String timeStampString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("d/M/yyyy");
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("d-M-yyyy");
        if (timeStampString.charAt(timeStampString.length() - 4) == '/') {
            if(timeStampString.charAt(timeStampString.length() - 6) == '/'){
                return dateFormat2.parse(timeStampString);
            } else
                return dateFormat.parse(timeStampString);
        }
        if (timeStampString.charAt(timeStampString.length() - 4) == '-') {
            if(timeStampString.charAt(timeStampString.length() - 6) == '-'){
                return dateFormat3.parse(timeStampString);
            } else
                return dateFormat1.parse(timeStampString);
        }
        return new Date();
    }

    List<Cell> getCellListFromRow(Row row){
        Iterator<Cell> cellIteratorOfDivisionNameRow = row.cellIterator();

        Iterable<Cell> iterable = () -> cellIteratorOfDivisionNameRow;

        return StreamSupport
                .stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
    }
}
