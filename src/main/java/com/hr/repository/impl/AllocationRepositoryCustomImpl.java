package com.hr.repository.impl;

import com.hr.model.Allocation;
import com.hr.model.dto.response.AllocationDetailResponseDTO;
import com.hr.model.dto.response.AllocationEmployeeResponseDTO;
import com.hr.model.dto.response.AllocationResponseDTO;
import com.hr.repository.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class AllocationRepositoryCustomImpl implements AllocationRepositoryCustom {
    @PersistenceContext
    EntityManager entityManager;

    private UserRepository userRepository;
    private ProjectRepository projectRepository;
    private OpportunityRepository opportunityRepository;
    private RoleRepository roleRepository;

    public AllocationRepositoryCustomImpl(EntityManager entityManager, UserRepository userRepository,
                                          ProjectRepository projectRepository, OpportunityRepository opportunityRepository, RoleRepository roleRepository) {
        this.entityManager = entityManager;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.opportunityRepository = opportunityRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public List<AllocationResponseDTO> search(String employeeNameToSearch, String projectNameToSearch, String pmNameToSearch, Timestamp fromStart, Timestamp toStart, Timestamp fromEnd, Timestamp toEnd, float startRate, float endRate, List<String> typesToSearch) {
        String subQuery = createQuery(fromStart, toStart, fromEnd, toEnd);
        if (!Strings.isBlank(subQuery)) {
            subQuery = " AND " + subQuery;
        }
        String subQueryType = createQueryType(typesToSearch);
        if (!Strings.isBlank(subQueryType)) {
            subQueryType = " AND " + subQueryType;
        }
        String query =
                "   SELECT\n" +
                        "       allocation.\"id\" AS allocation_id,\n" +
                        "       employee_table.\"id\" AS employee_id,\n" +
                        "       employee_table.employee_name AS employee_name,\n" +
                        "       \"role\".\"id\" AS role_id,\n" +
                        "       \"role\".role_name AS role_name,\n" +
                        "       allocation.allo AS allo,\n" +
                        "       employee_table.main_skill AS main_skill,\n" +
                        "       \"project\".\"id\" AS project_id,\n" +
                        "       \"project\".project_name, \n" +
                        "       \"project\".pm_id AS pm_project_id,\n" +
                        "       pm_table.employee_name AS pm_project_name,\n" +
                        "       \"opportunity\".\"id\" AS opportunity_id,\n" +
                        "       \"opportunity\".opp_name, \n" +
                        "       \"opportunity\".plan_pm_id AS pm_opportunity_id,\n" +
                        "       pm_opportunity_table.employee_name AS pm_opportunity_name,\n" +
                        "       allocation.start_date AS start_date,\n" +
                        "       allocation.end_date AS end_date,\n" +
                        "		employee_table.status,\n" +
                        "		allocation.rate,\n" +
                        "		allocation.note,\n" +
                        "		allocation.source,\n" +
                        "		employee_table.department,\n" +
                        "		allocation.type\n" +
                        "   \tFROM {h-schema}allocation \n" +
                        "       INNER JOIN {h-schema}\"user\" as employee_table on allocation.employee_id = employee_table.id\n" +
                        "       LEFT JOIN {h-schema}project on allocation.project_id = project.id\n" +
                        "       LEFT JOIN {h-schema}role on allocation.role_id = \"role\".id\n" +
                        "       LEFT JOIN {h-schema}\"user\" as pm_table on project.pm_id = pm_table.id\n" +
                        "       LEFT JOIN {h-schema}opportunity on allocation.opp_id = opportunity.id\n" +
                        "       LEFT JOIN {h-schema}\"user\" as pm_opportunity_table on opportunity.plan_pm_id = pm_opportunity_table.id\n" +
                        "   \tWHERE\n" +
                        "       allocation.is_deleted = 0" +
                        "       AND LOWER(employee_table.employee_name) LIKE '%' || ? || '%'\n" +
                        "       AND LOWER(concat(project.project_name,opportunity.opp_name)) LIKE '%' || ? || '%'\n" +
                        "       AND LOWER(concat(pm_table.employee_name,pm_opportunity_table.employee_name)) LIKE '%' || ? || '%'\n" +
                        "       AND allocation.rate >= ?" +
                        "       AND allocation.rate <= ?" +
                        subQuery +
                        subQueryType +
                        "ORDER BY opportunity_id, project_id, start_date DESC";

        Query q = entityManager.createNativeQuery(query);
        q.setParameter(1, employeeNameToSearch);
        q.setParameter(2, projectNameToSearch);
        q.setParameter(3, pmNameToSearch);
        q.setParameter(4, startRate);
        q.setParameter(5, endRate);


        List<Object> objects = q.getResultList();
        List<AllocationResponseDTO> allocationResponseDTOS = new ArrayList<>();
        for (Object o : objects) {
            Object[] ob = (Object[]) o;
            int projectId = ob[7] == null ? 0 : Integer.valueOf(ob[7].toString());
            String projectName = ob[8] == null ? "" : ob[8].toString();
            int pmId = ob[9] == null ? 0 : Integer.valueOf(ob[9].toString());
            String pmName = ob[10] == null ? "" : ob[10].toString();
            int oppId = ob[11] == null ? 0 : Integer.valueOf(ob[11].toString());
            String oppName = ob[12] == null ? "" : ob[12].toString();
            int planPmId = ob[13] == null ? 0 : Integer.valueOf(ob[13].toString());
            String planPmName = ob[14] == null ? "" : ob[14].toString();
            float rate = ob[18] == null ? 0 : Float.valueOf(ob[18].toString());
            String note = ob[19] == null ? "" : ob[19].toString();
            String source = ob[20] == null ? "" : ob[20].toString();
            String department = ob[21] == null ? "" : ob[21].toString();
            String type = ob[22] == null ? "" : ob[22].toString();
            AllocationResponseDTO allocationResponseDTO = new AllocationResponseDTO(
                    Integer.valueOf(ob[0].toString()), Integer.valueOf(ob[1].toString()), ob[2].toString(),
                    Integer.valueOf(ob[3].toString()), ob[4].toString(), Float.valueOf(ob[5].toString()), ob[6].toString(),
                    projectId, projectName, pmId, pmName, oppId, oppName, planPmId, planPmName,
                    Timestamp.valueOf(ob[15].toString()), Timestamp.valueOf(ob[16].toString()), ob[17].toString(), rate, note, source, department, type);
            allocationResponseDTOS.add(allocationResponseDTO);
        }
        return allocationResponseDTOS;
    }


    @Override
    public List<AllocationDetailResponseDTO> getListAllocationDetail(String employeeNameToSearch, String projectNameToSearch, List<Integer> roleIds, String pmNametoSearch, Timestamp startDateAllocationToSearch, Timestamp endDateAllocationToSearch) {
        List<AllocationDetailResponseDTO> allocationDetailResponseDTOS = new ArrayList<>();
        StringBuilder conditionQuery = new StringBuilder();
        if (employeeNameToSearch != null && !employeeNameToSearch.isEmpty()) {
            conditionQuery.append(" AND LOWER(emp.employee_name) LIKE '%").append(employeeNameToSearch).append("%'");
        }
        if (projectNameToSearch != null && !projectNameToSearch.isEmpty()) {
            conditionQuery.append(" AND (LOWER(prj.project_name) LIKE '%").append(projectNameToSearch).append("%' OR LOWER(opp.opp_name) LIKE '%").append(projectNameToSearch).append("%')");
        }
        if (pmNametoSearch != null && !pmNametoSearch.isEmpty()) {
            conditionQuery.append(" AND (LOWER(prj_pm.employee_name) LIKE '%").append(pmNametoSearch).append("%' OR LOWER(opp_pm.employee_name) LIKE '%").append(pmNametoSearch).append("%')");
        }
        if (roleIds != null && !roleIds.isEmpty()) {
            conditionQuery.append(createQueryRoleId(roleIds));
        }
        conditionQuery.append("AND(\n" +
                "(allocation.start_date > '" + new SimpleDateFormat("yyyy-MM-dd").format(startDateAllocationToSearch) + "' AND allocation.start_date < '" + new SimpleDateFormat("yyyy-MM-dd").format(endDateAllocationToSearch) + "')\n" +
                "OR\n" +
                "(allocation.end_date >'" + new SimpleDateFormat("yyyy-MM-dd").format(startDateAllocationToSearch) + "' AND allocation.end_date < '" + new SimpleDateFormat("yyyy-MM-dd").format(endDateAllocationToSearch) + "')\n" +
                "OR\n" +
                "(allocation.start_date < '" + new SimpleDateFormat("yyyy-MM-dd").format(startDateAllocationToSearch) + "' AND allocation.end_date > '" + new SimpleDateFormat("yyyy-MM-dd").format(endDateAllocationToSearch) + "')\n" +
                ")");
        String query = "SELECT DISTINCT\n" +
                "\tallocation.employee_id,\n" +
                "\temp.employee_name AS employee_name,\n" +
                "\tallocation.project_id,\n" +
                "\tprj.project_name,\n" +
                "\trole.role_name AS role_name,\n" +
                "\tprj_pm.employee_name AS prj_pm_name,\n" +
                "\tprj.start_date as prj_start_date,\n" +
                "\tprj.end_date as prj_end_date, \n" +
                "\tallocation.opp_id,\n" +
                "\topp.opp_name,\n" +
                "\topp_pm.employee_name AS opp_pm_name,\n" +
                "\topp.start_date as opp_start_date,\n" +
                "\topp.end_date as opp_end_date, \n" +
                "\temp.status, \n" +
                "\temp.main_skill \n" +
                "FROM\n" +
                "\t{h-schema}allocation\n" +
                "\tINNER JOIN {h-schema}\"user\" AS emp ON allocation.employee_id = emp.\"id\"\n" +
                "\tINNER JOIN {h-schema}role ON allocation.role_id = role.id\n" +
                "\tLEFT JOIN {h-schema}project as prj ON allocation.project_id = prj.\"id\"\n" +
                "\tLEFT JOIN {h-schema}\"user\" AS prj_pm ON prj.pm_id = prj_pm.\"id\"\n" +
                "\tLEFT JOIN {h-schema}opportunity as opp ON allocation.opp_id = opp.\"id\"\n" +
                "\tLEFT JOIN {h-schema}\"user\" AS opp_pm ON opp.plan_pm_id = opp_pm.\"id\"\n" +
                "WHERE allocation.is_deleted = 0 \n" + conditionQuery.toString() +
                " ORDER BY allocation.project_id, allocation.employee_id DESC";
        Query q = entityManager.createNativeQuery(query);

        List<Object> objects = q.getResultList();
        for (Object o : objects) {
            Object[] ob = (Object[]) o;
            int employeeId = ob[0] == null ? 0 : Integer.valueOf(ob[0].toString());
            String employeeName = ob[1] == null ? null : ob[1].toString();
            int projectId = ob[2] == null ? 0 : Integer.valueOf(ob[2].toString());
            String projectName = ob[3] == null ? ob[9].toString() : ob[3].toString();
            String roleName = ob[4] == null ? null : ob[4].toString();
            String pmName;
            if (Objects.isNull(ob[5])) {
                if (Objects.isNull(ob[10])) {
                    pmName = null;
                } else {
                    pmName = ob[10].toString();
                }
            } else {
                pmName = ob[5].toString();
            }

            Timestamp projectStartDate;
            Timestamp projectEndDate;
            if (Objects.isNull(ob[6])) {
                if (Objects.isNull(ob[11])) {
                    projectStartDate = null;
                } else {
                    projectStartDate = Timestamp.valueOf(ob[11].toString());
                }
            } else {
                projectStartDate = Timestamp.valueOf(ob[6].toString());
            }

            if (Objects.isNull(ob[7])) {
                if (Objects.isNull(ob[12])) {
                    projectEndDate = null;
                } else {
                    projectEndDate = Timestamp.valueOf(ob[12].toString());
                }
            } else {
                projectEndDate = Timestamp.valueOf(ob[7].toString());
            }

            if (ob[8] != null && Integer.valueOf(ob[8].toString()) != 0) {
                projectId = Integer.valueOf(ob[8].toString()) * 1000;
            }
            String status = ob[13].toString();
            String mainSkill = ob[14].toString();

            AllocationDetailResponseDTO allocationDetailResponseDTO = new AllocationDetailResponseDTO(employeeId,
                    employeeName, projectId, projectName, roleName, pmName, projectStartDate, projectEndDate, status, mainSkill);
            allocationDetailResponseDTOS.add(allocationDetailResponseDTO);
        }
        return allocationDetailResponseDTOS;
    }

    @Override
    public List<AllocationDetailResponseDTO> getListFreeAllocationDetail(String employeeNameToSearch, String projectNameToSearch,
                                                                         List<Integer> roleIds, String pmNameToSearch, Timestamp startDateToSearch, Timestamp endDateToSearch) {
        List<AllocationDetailResponseDTO> allocationDetailResponseDTOS = new ArrayList<>();
        StringBuilder conditionQuery = new StringBuilder();
        StringBuilder projectCondition = new StringBuilder(); // TODO: Khi nhap ten project va thuc hien search free employee
        // TODO thi dieu kien hien thi dang la member du an ranh trong tat ca du an ho tham gia

        if (employeeNameToSearch != null && !employeeNameToSearch.isEmpty()) {
            conditionQuery.append(" AND LOWER(emp.employee_name) LIKE '%").append(employeeNameToSearch).append("%'\n");
        }
        if (projectNameToSearch != null && !projectNameToSearch.isEmpty()) {
            conditionQuery.append(" AND (LOWER(project.project_name) LIKE '%").append(projectNameToSearch).append("%' OR LOWER(opp.opp_name) LIKE '%").append(projectNameToSearch).append("%')\n");
        }
        if (pmNameToSearch != null && !pmNameToSearch.isEmpty()) {
            conditionQuery.append(" AND (LOWER(prj_pm.employee_name) LIKE '%").append(pmNameToSearch).append("%' OR LOWER(opp_pm.employee_name) LIKE '%").append(pmNameToSearch).append("%')\n");
        }
        if (roleIds != null && !roleIds.isEmpty()) {
            conditionQuery.append(createQueryRoleId(roleIds));
        }

        String query =
                "   WITH full_date AS ( SELECT generate_series ( CAST(:startDate AS TIMESTAMP), CAST (:endDate AS TIMESTAMP), '1 day' ) AS allo_date )\n" +
                        "   SELECT DISTINCT\n" +
                        "   emp.id AS employee_id,\n" +
                        "   emp.employee_name AS employee_name,\n" +
                        "   project.id AS project_id,\n" +
                        "   project.project_name,\n" +
                        "   role.role_name AS role_name,\n" +
                        "   prj_pm.employee_name AS pm_name,\n" +
                        "   project.start_date,\n" +
                        "   project.end_date,\n" +
                        "   allocation.opp_id,\n" +
                        "   opp.opp_name,\n" +
                        "   opp_pm.employee_name AS opp_pm_name,\n" +
                        "   opp.start_date as opp_start_date,\n" +
                        "   opp.end_date as opp_end_date, \n" +
                        "   emp.status,\n" +
                        "   emp.main_skill AS main_skill\n" +
                        "   FROM\n" +
                        "   {h-schema}\"user\" AS emp" +
                        "   INNER JOIN {h-schema}allocation ON emp.id = allocation.employee_id\n" +
                        "   LEFT JOIN {h-schema}project ON allocation.project_id = project.id\n" +
                        "   LEFT JOIN {h-schema}\"user\" AS prj_pm ON project.pm_id = prj_pm.id\n" +
                        "   LEFT JOIN {h-schema}opportunity as opp ON allocation.opp_id = opp.id\n" +
                        "   LEFT JOIN {h-schema}\"user\" AS opp_pm ON opp.plan_pm_id = opp_pm.id\n" +
                        "   INNER JOIN {h-schema}role ON allocation.role_id = role.id\n" +
                        "   WHERE\n" +
                        "   emp.status = '1'\n" +
                        "   AND emp.is_deleted = 0\n" +
                        conditionQuery.toString() +
                        "   AND " +
                        "   (   employee_id IS NULL" +
                        "       OR (" +
                        "           allocation.is_deleted = 0" +
                        "           AND" +
                        "               ( SELECT COUNT ( * ) FROM full_date ) != " +
                        "               ( SELECT COUNT ( * ) FROM (" +
                        "                   SELECT DISTINCT employee_id, to_char(allo_date, 'yyyy-mm-dd')" +
                        "                   FROM {h-schema}allocation_detail" +
                        "                   WHERE allo_date >= CAST(:startDate AS TIMESTAMP) AND allo_date < CAST(:endDate AS TIMESTAMP) + INTERVAL '1 day' AND allocation_detail.employee_id = emp.id " +
                        projectCondition +
                        " ) AS emp_work_date_count )" +
                        "       ))" +
                        " ORDER BY project_id, employee_id DESC";
        Query q = entityManager.createNativeQuery(query);
        q.setParameter("startDate", removeHourMinuteSecondFromATimeStamp(startDateToSearch), TemporalType.TIMESTAMP);
        q.setParameter("endDate", removeHourMinuteSecondFromATimeStamp(endDateToSearch), TemporalType.TIMESTAMP);

        List<Object> objects = q.getResultList();

        for (Object o : objects) {
            Object[] ob = (Object[]) o;
            int employeeId = ob[0] == null ? 0 : Integer.valueOf(ob[0].toString());
            String employeeName = ob[1] == null ? "" : ob[1].toString();
            int projectId = ob[2] == null ? 0 : Integer.valueOf(ob[2].toString());
            String projectName = ob[3] == null ? "" : ob[3].toString();
            String roleName = ob[4] == null ? "" : ob[4].toString();
            String pmName = ob[5] == null ? "" : ob[5].toString();

            Timestamp projectStartDate;
            Timestamp projectEndDate;

            if (Objects.isNull(ob[6])) {
                if (Objects.isNull(ob[11])) {
                    projectStartDate = null;
                } else {
                    projectStartDate = Timestamp.valueOf(ob[11].toString());
                }
            } else {
                projectStartDate = Timestamp.valueOf(ob[6].toString());
            }

            if (Objects.isNull(ob[7])) {
                if (Objects.isNull(ob[12])) {
                    projectEndDate = null;
                } else {
                    projectEndDate = Timestamp.valueOf(ob[12].toString());
                }
            } else {
                projectEndDate = Timestamp.valueOf(ob[7].toString());
            }

            if (!Objects.isNull(ob[8]) && Integer.valueOf(ob[8].toString()) != 0) {
                projectId = Integer.valueOf(ob[8].toString()) * 1000;
            }
            String oppName = ob[9] == null ? "" : ob[9].toString();
            projectName += oppName;
            String oppPmName = ob[10] == null ? "" : ob[10].toString();
            pmName += oppPmName;
            String status = ob[13] == null ? "" : ob[13].toString();
            String mainSkill = ob[14] == null ? "" : ob[14].toString();

            AllocationDetailResponseDTO allocationDetailResponseDTO = new AllocationDetailResponseDTO(employeeId,
                    employeeName, projectId, projectName, roleName, pmName, projectStartDate, projectEndDate, status, mainSkill);
            allocationDetailResponseDTOS.add(allocationDetailResponseDTO);
        }
        return allocationDetailResponseDTOS;
    }

    @Override
    public List<AllocationEmployeeResponseDTO> getListAllocationEmployee(String projectNameToSearch, String employeeNameToSearch) {
        List<AllocationEmployeeResponseDTO> allocationEmployeeResponseDTOS = new ArrayList<>();
        StringBuilder conditionQuery = new StringBuilder();

        if (employeeNameToSearch != null && !employeeNameToSearch.isEmpty()) {
            conditionQuery.append(" AND LOWER(emp_table.employee_name) LIKE '%").append(employeeNameToSearch).append("%'");
        }
        if (projectNameToSearch != null && !projectNameToSearch.isEmpty()) {
            conditionQuery.append(" AND (LOWER(project.project_name) LIKE '%").append(projectNameToSearch).append("%' OR LOWER(opp.opp_name) LIKE '%").append(projectNameToSearch).append("%')");
        }

        String query =
                "SELECT " +
                        "allocation.employee_id as employee_id,\n" +
                        "emp_table.employee_name as employee_name,\n" +
                        "allocation.project_id as project_id,\n" +
                        "COALESCE (project.project_name, opp.opp_name) as project_name,\n" +
                        "COALESCE (project.pm_id, opp.plan_pm_id) as pm_id,\n" +
                        "COALESCE (pm_pr_table.employee_name, pm_opp_table.employee_name) as pm_name,\n" +
                        "allocation.start_date as start_date,\n" +
                        "allocation.end_date as end_date,\n" +
                        "allocation.allo,\n" +
                        "allocation.opp_id as opp_id\n" +
                        "FROM\n" +
                        "   {h-schema}allocation\n" +
                        "   INNER JOIN {h-schema}\"user\" as emp_table ON allocation.employee_id = emp_table.id\n" +
                        "   LEFT JOIN {h-schema}project ON allocation.project_id = project.id\n" +
                        "   LEFT JOIN {h-schema}\"user\" as pm_pr_table ON project.pm_id = pm_pr_table.id\n" +
                        "   LEFT JOIN {h-schema}opportunity as opp ON allocation.opp_id = opp.id\n" +
                        "   LEFT JOIN {h-schema}\"user\" as pm_opp_table ON opp.plan_pm_id = pm_opp_table.id\n" +
                        "   WHERE allocation.is_deleted = 0 \n" + conditionQuery.toString();
        Query q = entityManager.createNativeQuery(query);

        List<Object> objects = q.getResultList();
        for (Object o : objects) {
            Object[] ob = (Object[]) o;
            int employeeId = ob[0] == null ? 0 : Integer.valueOf(ob[0].toString());
            String employeeName = ob[1] == null ? null : ob[1].toString();
            int projectId = ob[2] == null ? 0 : Integer.valueOf(ob[2].toString());
            String projectName = ob[3] == null ? null : ob[3].toString();
            int pmId = ob[2] == null ? 0 : Integer.valueOf(ob[2].toString());
            String pmName = ob[5] == null ? null : ob[5].toString();
            Timestamp startDate = ob[6] == null ? null : Timestamp.valueOf(ob[6].toString());
            Timestamp endDate = ob[7] == null ? null : Timestamp.valueOf(ob[7].toString());
            float allocation = ob[8] == null ? 0 : Float.valueOf(ob[8].toString());
            if (!Objects.isNull(ob[9])) {
                projectId = Integer.valueOf(ob[9].toString()) * 1000;
            }
            AllocationEmployeeResponseDTO allocationEmployeeResponseDTO = new AllocationEmployeeResponseDTO(employeeId, employeeName, projectId,
                    projectName, pmId, pmName, startDate, endDate, allocation);
            allocationEmployeeResponseDTOS.add(allocationEmployeeResponseDTO);
        }
        return allocationEmployeeResponseDTOS;
    }

    @Override
    public List<AllocationResponseDTO> list() {
        String query = "SELECT\n" +
                "allocation.id,\n" +
                "allocation.employee_id,\n" +
                "employee_table.employee_name,\n" +
                "allocation.role_id,\n" +
                "\"role\".role_name,\n" +
                "allocation.allo,\n" +
                "employee_table.main_skill,\n" +
                "allocation.project_id,\n" +
                "project.project_name,\n" +
                "\"project\".pm_id,\n" +
                "pm_project_table.employee_name as pm_name,\n" +
                "allocation.opp_id,\n" +
                "opportunity.opp_name,\n" +
                "\"opportunity\".plan_pm_id,\n" +
                "pm_opportunity_table.employee_name as plan_pm_name,\n" +
                "allocation.start_date,\n" +
                "allocation.end_date,\n" +
                "employee_table.status,\n" +
                "allocation.rate,\n" +
                "allocation.note,\n" +
                "allocation.source,\n" +
                "employee_table.department,\n" +
                "allocation.type\n" +
                "FROM\n" +
                "{h-schema}allocation\n" +
                "INNER JOIN {h-schema}\"user\" as employee_table\n" +
                "ON allocation.employee_id = employee_table.\"id\"\n" +
                "INNER JOIN {h-schema}\"role\"\n" +
                "ON allocation.role_id = \"role\".\"id\"\n" +
                "LEFT JOIN {h-schema}\"project\"\n" +
                "ON allocation.project_id = \"project\".\"id\"\n" +
                "LEFT JOIN {h-schema}\"user\" as pm_project_table\n" +
                "ON \"project\".pm_id = pm_project_table.\"id\"\n" +
                "LEFT JOIN {h-schema}\"opportunity\"\n" +
                "ON allocation.opp_id = \"opportunity\".\"id\"\n" +
                "LEFT JOIN {h-schema}\"user\" as pm_opportunity_table\n" +
                "ON \"opportunity\".plan_pm_id = pm_opportunity_table.\"id\"\n" +
                "WHERE allocation.is_deleted = 0 \n" +
                "ORDER BY allocation.opp_id, allocation.project_id, allocation.start_date DESC";

        Query q = entityManager.createNativeQuery(query);

        List<Object> objects = q.getResultList();

        List<AllocationResponseDTO> allocationResponseDTOS = new ArrayList<>();
        for (Object o : objects) {
            Object[] ob = (Object[]) o;
            int projectId = ob[7] == null ? 0 : Integer.valueOf(ob[7].toString());
            String projectName = ob[8] == null ? null : ob[8].toString();
            int pmId = ob[9] == null ? 0 : Integer.valueOf(ob[9].toString());
            String pmName = ob[10] == null ? null : ob[10].toString();
            int oppId = ob[11] == null ? 0 : Integer.valueOf(ob[11].toString());
            String oppName = ob[12] == null ? null : ob[12].toString();
            int planPmId = ob[13] == null ? 0 : Integer.valueOf(ob[13].toString());
            String planPmName = ob[14] == null ? null : ob[14].toString();
            float rate = ob[18] == null ? 0 : Float.valueOf(ob[18].toString());
            String note = ob[19] == null ? "" : ob[19].toString();
            String source = ob[20] == null ? "" : ob[20].toString();
            String department = ob[21] == null ? "" : ob[21].toString();
            String type = department = ob[22] == null ? "" : ob[22].toString();
            AllocationResponseDTO allocationResponseDTO = new AllocationResponseDTO(
                    Integer.valueOf(ob[0].toString()), Integer.valueOf(ob[1].toString()), ob[2].toString(),
                    Integer.valueOf(ob[3].toString()), ob[4].toString(), Float.valueOf(ob[5].toString()), ob[6].toString(),
                    projectId, projectName, pmId, pmName, oppId, oppName, planPmId, planPmName,
                    Timestamp.valueOf(ob[15].toString()), Timestamp.valueOf(ob[16].toString()), ob[17].toString(), rate, note, source, department, type);
            allocationResponseDTOS.add(allocationResponseDTO);
        }
        return allocationResponseDTOS;
    }

    private String createQuery(Timestamp fromStart, Timestamp toStart, Timestamp fromEnd, Timestamp toEnd) {
        StringBuilder sbQuery = new StringBuilder();
        if (Objects.isNull(fromStart)) {
            if (!Objects.isNull(toStart)) {
                sbQuery.append("(allocation.start_date < '")
                        .append(toStart.toString())
                        .append("')");
            }
        } else {
            if (Objects.isNull(toStart)) {
                sbQuery.append("(allocation.start_date > '")
                        .append(fromStart.toString())
                        .append("')");
            } else {
                sbQuery.append("(allocation.start_date > '")
                        .append(fromStart.toString())
                        .append("' and allocation.start_date < '")
                        .append(toStart.toString())
                        .append("')");
            }
        }

        if ((!Objects.isNull(fromStart) || !Objects.isNull(toStart)) && (!Objects.isNull(fromEnd) || !Objects.isNull(toEnd))) {
            sbQuery.append("and");
        }
        if (Objects.isNull(fromEnd)) {
            if (!Objects.isNull(toEnd)) {
                sbQuery.append("(allocation.end_date < '")
                        .append(toEnd.toString())
                        .append("')");
            }
        } else {
            if (Objects.isNull(toEnd)) {
                sbQuery.append("(allocation.end_date > '")
                        .append(fromEnd.toString())
                        .append("')");
            } else {
                sbQuery.append("(allocation.end_date > '")
                        .append(fromEnd.toString())
                        .append("' and allocation.end_date < '")
                        .append(toEnd.toString())
                        .append("')");
            }
        }
        return sbQuery.toString();
    }

    private StringBuilder createQueryRoleId(List<Integer> roleIds) {
        StringBuilder sb = new StringBuilder();
        sb.append(" AND allocation.role_id IN (");
        String prefix = "";
        for (int roleId : roleIds) {
            sb.append(prefix);
            prefix = ",";
            sb.append(roleId);
        }
        sb.append(") \n");
        return sb;
    }


    @Override
    public boolean findAllocationByTypeAndEmployeeIdAndProjectIdAndStartDateAndEndDate(String type, int employeeId, int projectId, Timestamp startDate, Timestamp endDate) {
        String startDateString = startDate.toString().substring(0, 10);
        String endDateString = endDate.toString().substring(0, 10);

        String subQuery = createSubQueryForFindDuplicateAllocation(employeeId, projectId, startDateString, endDateString);

        String query = "SELECT *\n" +
                "FROM\n" +
                "{h-schema}allocation\n" +
                "WHERE allocation.is_deleted = 0 and allocation.type = \n" + type +
                subQuery +
                "ORDER BY allocation.opp_id, allocation.project_id, allocation.start_date DESC";

        Query q = entityManager.createNativeQuery(query);

        List<Object> objects = q.getResultList();
        if (objects.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean
    findAllocationByEmployeeIdAndProjectIdAndStartDateAndEndDate(int employeeId, int projectId, Timestamp startDate, Timestamp endDate) {
        String startDateString = startDate.toString().substring(0, 10);
        String endDateString = endDate.toString().substring(0, 10);

        String subQuery = createSubQueryForFindDuplicateAllocation(employeeId, projectId, startDateString, endDateString);

        String query = "SELECT *\n" +
                "FROM\n" +
                "{h-schema}allocation\n" +
                "WHERE allocation.is_deleted = 0 \n" +
                subQuery +
                "ORDER BY allocation.opp_id, allocation.project_id, allocation.start_date DESC";

        Query q = entityManager.createNativeQuery(query);

        List<Object> objects = q.getResultList();
        if (objects.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public List<Allocation> getListAllocationByProjectIdAndTypeAndRoles(int projectId, String type, List<Integer> roleIds) {
        String subQuery = createSubQueryForFindAllocationByRoles(roleIds);

        String query ="SELECT\n" +
                "allocation.id,\n" +
                "allocation.employee_id,\n" +
                "allocation.role_id,\n" +
                "allocation.allo,\n" +
                "allocation.project_id,\n" +
                "allocation.start_date,\n" +
                "allocation.end_date,\n" +
                "allocation.created_at,\n" +
                "allocation.created_by,\n" +
                "allocation.updated_at,\n" +
                "allocation.updated_by,\n" +
                "allocation.is_deleted,\n" +
                "allocation.opp_id,\n" +
                "allocation.rate,\n" +
                "allocation.note,\n" +
                "allocation.source,\n" +
                "allocation.type\n" +
                "FROM\n" +
                "{h-schema}allocation\n" +
                "WHERE allocation.is_deleted = 0 \n" +
                "AND allocation.project_id = ? \n" +
                "AND allocation.type = ? \n" +
                subQuery +
                "ORDER BY allocation.id DESC" ;

        Query q = entityManager.createNativeQuery(query);

        q.setParameter(1, projectId);
        q.setParameter(2, type);
        List<Object> objects = q.getResultList();

        List<Allocation> allocations = new ArrayList<>();

        for (Object o : objects) {
            Object[] ob = (Object[]) o;

            int id = ob[0] == null ? 0 : Integer.valueOf(ob[0].toString());
            int employeeId = ob[1] == null ? 0 : Integer.valueOf(ob[1].toString());
            int roleId = ob[2] == null ? 0 : Integer.valueOf(ob[2].toString());
            float allo = ob[3] == null ? 0 : Float.valueOf(ob[3].toString());
//            int projectId = ob[4] == null ? 0 : Integer.valueOf(ob[2].toString());
            Timestamp startDate = ob[5] == null ? null : Timestamp.valueOf(ob[5].toString());
            Timestamp endDate = ob[6] == null ? null : Timestamp.valueOf(ob[6].toString());
            Timestamp createdAt = ob[7] == null ? null : Timestamp.valueOf(ob[7].toString());
            int createdBy = ob[8] == null ? 0 : Integer.valueOf(ob[8].toString());
            Timestamp updatedAt = ob[9] == null ? null : Timestamp.valueOf(ob[9].toString());
            int updatedBy = ob[10] == null ? 0 : Integer.valueOf(ob[10].toString());
            int isDeleted = ob[11] == null ? 0 : Integer.valueOf(ob[11].toString());
            int oppId = ob[12] == null ? 0 : Integer.valueOf(ob[12].toString());
            float rate  = ob[13] == null ? 0 : Float.valueOf(ob[13].toString());
            String note = ob[14] == null ? "" : ob[14].toString();
            String source = ob[15] == null ? "" : ob[15].toString();

            Allocation allocation = new Allocation(userRepository.findAllById(employeeId), projectRepository.findAllById(projectId),
                    opportunityRepository.findAllById(oppId), roleRepository.findAllById(roleId), allo, startDate, endDate,
                    rate, note, source, type);

            allocation.setId(id);
            allocation.setCreatedAt(createdAt);
            allocation.setCreatedBy(createdBy);
            allocation.setUpdatedAt(updatedAt);
            allocation.setUpdatedBy(updatedBy);

            allocations.add(allocation);
        }
        return allocations;
    }

    private String createSubQueryForFindDuplicateAllocation(int employeeId, int projectId, String startDate, String endDate) {
        StringBuilder sbQuery = new StringBuilder();

        sbQuery.append("AND allocation.employee_id = ")
                .append(employeeId);

        sbQuery.append(" AND allocation.project_id = ")
                .append(projectId);


        sbQuery.append(" AND to_char(allocation.start_date, 'yyyy-mm-dd') LIKE '%")
                .append(startDate)
                .append("%'");

        sbQuery.append(" AND to_char(allocation.end_date, 'yyyy-mm-dd') LIKE '%")
                .append(endDate)
                .append("%'");
        return sbQuery.toString();
    }

    private String createQueryType(List<String> typesToSearch) {
        StringBuilder sbQuery = new StringBuilder();
        int typesToSearchSize = typesToSearch.size();
        if (!typesToSearch.isEmpty()) {
            sbQuery.append("allocation.type IN (");
            for (int i = 0; i < typesToSearchSize - 1; i++) {
                sbQuery.append("'").append(typesToSearch.get(i)).append("',");
            }
            sbQuery.append("'").append(typesToSearch.get(typesToSearchSize - 1)).append("')");
        }
        return sbQuery.toString();
    }

    private String createSubQueryForFindAllocationByRoles(List<Integer> roleIds) {
        StringBuilder sbQuery = new StringBuilder();

        if(!roleIds.isEmpty()){
            sbQuery.append("AND allocation.role_id IN ( ");

            if(roleIds.size() == 1){
                sbQuery.append(roleIds.get(0)).append(") ");
                return sbQuery.toString();
            }

            for(int i=0; i< roleIds.size()-1; i++){
                sbQuery.append(roleIds.get(i)).append(", ");
            }
            sbQuery.append(roleIds.get(roleIds.size()-1)).append(")");
        }
        return sbQuery.toString();
    }

    private Timestamp removeHourMinuteSecondFromATimeStamp(Timestamp timestamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp.getTime()));
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }
}