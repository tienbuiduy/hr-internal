package com.hr.constant;

public class CommonConstant {
    public static final String GOOGLE_LINK_AUTHEN = "oauth2/authorization/google";
    public static final int NOT_DELETED = 0;

    public class AppParams {
        public static final String COUNTRY_TYPE = "COUNTRY_TYPE";
        public static final String CUSTOMER_RANK_TYPE = "CUSTOMER_RANK_TYPE";
        public static final String OPPORTUNITY_STATUS = "OPPORTUNITY_STATUS";
        public static final String EMPLOYEE_STATUS = "EMPLOYEE_STATUS";
        public static final String PROJECT_RANK_TYPE = "PROJECT_RANK_TYPE";
        public static final String ALLOCATION_TYPE = "ALLOCATION_TYPE";
    }

    public class OpportunityStatus {
        public static final String NEW = "1";
        public static final String INPROGRESS = "2";
        public static final String SEND_ESTIMATE = "3";
        public static final String WIN = "4";
        public static final String LOSE = "5";
    }

    public class EmployeeStatus {
        public static final String WORKING = "1";
        public static final String RESIGN = "0";
        public static final String TEMPORARY_LEAVING = "2";
    }

    public class ProjectStatus {
        public static final String OPEN = "1";
        public static final String CLOSED = "0";
        public static final String PENDING = "2";
    }

    public class ErrorMessage {
        public static final String EMAIL_IS_EXIST = "Email address already exists";
        public static final String OPPORTUNITY_NAME_IS_EXIST = "Opportunity name already exists";
        public static final String ENTER_PROJECT_OR_OPPORTUNITY_ONLY = "Please enter Project or Opportunity only";
        public static final String NOT_AUTHORIZED = "You are not authorized to access this function";
        public static final String PREFIX_IMPORT_ERROR = "Cannot import following records:\n";
        public static final String REQUIRE_ROLE_DM_PMO_TO_UPDATE_ROLE = "Require DM / PMO to update employee role";
        public static final String REQUIRE_ROLE_DM_PMO_TO_CREATE_DM_PMO = "Require DM / PMO to create user with role PM / DMO";
        public static final String IMPORT_ALLOCATION_FAILED = "An error occurred in the import.\nCan not get path to save file on server.";
        public static final String IMPORT_ALLOCATION_SUCCESS = "Import allocation successfully";
        public static final String IMPORT_LESSON_LEARN_SUCCESS = "Import lesson learn successfully";
        public static final String PROJECT_EE_TEMPLATE_NOT_EXIST = "Project EE template not exists";
        public static final String PROJECT_EE_EXPORT_PATH_NOT_EXIST = "Project EE export path not exists";
        public static final String ALLOCATION_IMPORT_PATH_NOT_EXIST = "Allocation import path not exists";
        public static final String LESSON_LEARN_IMPORT_PATH_NOT_EXIST = "Lessons learn import path not exists";
        public static final String LESSONLEARNS_PROJECT_IMPORT_NOT_EXISTS = "Project not exists";
        public static final String FIELD_IS_EMPTY = "field is empty";
        public static final String WRONG_FORMAT_DATE = "Wrong format date";
        public static final String WRONG_FORMAT_START_DATE = "Wrong format start date";
        public static final String WRONG_FORMAT_END_DATE = "Wrong format end date";
        public static final String INVALID_ALLOCATION_IMPORT_TEMPLATE = "Template upload invalid.\n Required fields: #, Employee, Work Email," +
                " Department, Start Date, End Date, Project Role, Allocation (%), Performance rate, Qualified, Note, Project, Nguồn, Allocation type";
        public static final String INVALID_LESSON_LEARN_IMPORT_TEMPLATE = "Template upload invalid.\n Required fields: #, Date," +
                " Description, Category, Impact, Root cause, Corrective action/Prevention action, PIC, Deadline, Expected result," +
                " Actual result, Status, Opinion by Division's PMO, Other notes, Nguồn gốc vấn đề, Long term solution";
        public static final String INVALID_ALLOCATION_BY_DAY_IMPORT_TEMPLATE = "Template upload invalid.\n Required fields: Mã nhân viên, Tên nhân viên, Tên dự án," +
                " Project role, Performance rate, Đơn vị";
        public static final String INVALID_ALLOCATION_BY_PERIOD_IMPORT_TEMPLATE = "Template upload invalid.\n Required fields: Mã nhân viên, Tên nhân viên, Tên dự án," +
                " Đơn vị";
        public static final String INVALID_ALLOCATION_BY_MONTH_IMPORT_TEMPLATE = "Template upload invalid.\n Required fields: Allocation type, Tên dự án, Mã nhân viên," +
                " Role, Name, Performance, Đơn vị allocation";
    }

    public class RoleCode {
        public static final String TECH_LEAD = "TL";
        public static final String SWAT = "SWAT";
        public static final String TESTER = "TEST";
        public static final String PROJECT_MANAGER = "PM";
        public static final String DEVELOPER = "DEV";
        public static final String DIRECTOR = "DIR";
        public static final String DIVISION_MANAGER = "DM";
        public static final String QA = "QA";
        public static final String COMTOR = "COMT";
        public static final String PMO = "PMO";
        public static final String HR = "HR";
    }

    public class RoleId {
        public static final int DEVELOPER = 1;
        public static final int TECH_LEAD = 2;
        public static final int DIRECTOR = 3;
        public static final int SWAT = 4;
        public static final int TESTER = 5;
        public static final int PROJECT_MANAGER = 6;
        public static final int DIVISION_MANAGER = 7;
        public static final int QA = 8;
        public static final int COMTOR = 9;
        public static final int PMO = 10;
        public static final int HR = 11;
        public static final int PO = 12;
    }

    public class Department {
        public static final String OS3 = "OS3";
    }

    public class FilePath {
        // Kiem tra cac thu muc tren server truoc khi push code

        public static final String PROJECT_EE_TEMPLATE = "/var/internal/template/allocation/Project-EE-Template.xlsx";
        public static final String ALLOCATION_BY_DAY_TEMPLATE = "/var/internal/template/allocation/Allocation-By-Day-Template.xlsx";
        public static final String ALLOCATION_BY_MONTH_TEMPLATE = "/var/internal/template/allocation/Allocation-By-Month-Template.xlsx";
        public static final String ALLOCATION_BY_PERIOD_TEMPLATE = "/var/internal/template/allocation/Allocation-By-Period-Time-Template.xlsx";

        public static final String ALLOCATION_IMPORT_PATH = "/var/internal/import/allocation";
        public static final String EXPORT_PATH = "/var/internal/export/allocation";
        public static final String LESSON_LEARN_IMPORT_PATH = "/var/internal/import/lesson-learn";

        public static final String PROJECT_EE_EXPORT = "/var/internal/export/allocation/Project-EE-Export.xlsx";
        public static final String ALLOCATION_BY_DAY_EXPORT = "/var/internal/export/allocation/Allocation-By-Day-Export.xlsx";
        public static final String ALLOCATION_BY_MONTH_EXPORT = "/var/internal/export/allocation/Allocation-By-Month-Export.xlsx";
        public static final String ALLOCATION_BY_PERIOD_EXPORT = "/var/internal/export/allocation/Allocation-By-Period-Time-Export.xlsx";

        public static final String PROJECT_EE_TEMPLATE_TEMPORARY_FILE_NAME = "Project-EE-Export.xlsx";
        public static final String ALLOCATION_BY_DAY_EXPORT_FILE_NAME = "Allocation-Export-By-Day.xlsx";
        public static final String ALLOCATION_BY_MONTH_EXPORT_FILE_NAME = "Allocation-By-Month-Export.xlsx";
        public static final String ALLOCATION_BY_PERIOD_EXPORT_FILE_NAME = "Allocation-By-Period-Time-Export.xlsx";


//        public static final String PROJECT_EE_TEMPLATE = "E:/Internal Project/import-export-folder/template/Project-EE-Template.xlsx";
//        public static final String ALLOCATION_BY_DAY_TEMPLATE = "E:/Internal Project/import-export-folder/template/Allocation-By-Day-Template.xlsx";
//        public static final String ALLOCATION_BY_MONTH_TEMPLATE = "E:/Internal Project/import-export-folder/template/Allocation-By-Month-Template.xlsx";
//        public static final String ALLOCATION_BY_PERIOD_TEMPLATE = "E:/Internal Project/import-export-folder/template/Allocation-By-Period-Time-Template.xlsx";
//
//        public static final String ALLOCATION_IMPORT_PATH = "E:/Internal Project/import-export-folder/import/allocation";
//        public static final String LESSON_LEARN_IMPORT_PATH = "E:/Internal Project/import-export-folder/import/lesson-learn";
//        public static final String EXPORT_PATH = "E:/Internal Project/import-export-folder/export";
//
//        public static final String PROJECT_EE_EXPORT = "E:/Internal Project/import-export-folder/export/Project-EE-Export.xlsx";
//        public static final String ALLOCATION_BY_DAY_EXPORT = "E:/Internal Project/import-export-folder/export/Allocation-By-Day-Export.xlsx";
//        public static final String ALLOCATION_BY_MONTH_EXPORT = "E:/Internal Project/import-export-folder/export/Allocation-By-Month-Export.xlsx";
//        public static final String ALLOCATION_BY_PERIOD_EXPORT = "E:/Internal Project/import-export-folder/export/Allocation-By-Period-Time-Export.xlsx";
//
//        public static final String PROJECT_EE_TEMPLATE_TEMPORARY_FILE_NAME = "Project-EE-Export.xlsx";
//        public static final String ALLOCATION_BY_DAY_EXPORT_FILE_NAME = "Allocation-Export-By-Day.xlsx";
//        public static final String ALLOCATION_BY_MONTH_EXPORT_FILE_NAME = "Allocation-By-Month-Export.xlsx";
//        public static final String ALLOCATION_BY_PERIOD_EXPORT_FILE_NAME = "Allocation-By-Period-Time-Export.xlsx";
    }

    public class EECalculator {
        public static final int INDEX_OF_HEADER_ROW_OF_EECALCULATOR_BY_PROJECT = 4;
        public static final int DISTANCE_BETWEEN_LAST_DATA_ROW_OF_FIRST_TABLE_AND_FIRST_DATA_ROW_OF_SECOND_TABLE = 5;
        public static final int INDEX_OF_LAST_COL_OF_EECALCULATOR_BY_PROJECT = 7;
    }

    public class FileExtensions {
        public static final String EXCEL_FILE_EXTENSION = "%s.xlsx";
    }

    public class AllocationType {
        public static final String ACTUAL = "Actual";
        public static final String PLAN = "Plan";
    }

    public class Template {
        public static final int ALLOCATION_IMPORT_TEMPLATE_ROW_NUMS = 14;
        public static final int LESSON_LEARN_IMPORT_TEMPLATE_ROW_NUMS = 16;
        public static final int ALLOCATION_BY_DAY_IMPORT_TEMPLATE_NUMBER_OF_TITLE_ELEMENTS = 6;
        public static final int ALLOCATION_BY_PERIOD_IMPORT_TEMPLATE_NUMBER_OF_TITLE_ELEMENTS = 4;
        public static final int ALLOCATION_BY_MONTH_IMPORT_TEMPLATE_NUMBER_OF_TITLE_ELEMENTS = 7;
        public static final int PROJECT_EE_INDEX_OF_FIRST_DATA_ROW_OF_PROJECT_EE_CALCULATOR = 4;
    }

    public class AllocationExport {
        public static final int TOTAL_WORKING_HOURS_OF_A_DAY = 8;
    }

    public class AllocationUnit{
        public static final String PERCENT_TYPE = "%";
        public static final String MM = "mm";
        public static final String HOUR = "h";
        public static final int PERCENT = 100;
        public static final int TOTAL_HOUR = 8*20;
    }
}

