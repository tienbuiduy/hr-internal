package com.hr.repository.impl;

import com.hr.constant.CommonConstant;
import com.hr.model.dto.response.OpportunityResponseDTO;
import com.hr.repository.OpportunityRepositoryCustom;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Repository
public class OpportunityRepositoryCustomImpl implements OpportunityRepositoryCustom {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<OpportunityResponseDTO> search(String oppName, int contractTypeId, int planPmId, int tempPmId, Timestamp fromStartDate,
                                               Timestamp toStartDate, Timestamp fromEndDate, Timestamp toEndDate, List<String> listStatus) {
        String subQuery = createQuery(oppName, contractTypeId, planPmId, tempPmId, fromStartDate, toStartDate, fromEndDate, toEndDate, listStatus);
        if (!Strings.isBlank(subQuery)) {
            subQuery = " WHERE " + subQuery;
        }

        List<OpportunityResponseDTO> opportunityResponseDTOS = new ArrayList<>();

        String query = "SELECT\n" +
                "opportunity.id,\n" +
                "opportunity.opp_code,\n" +
                "opportunity.opp_name,\n" +
                "opportunity.contract_type_id,\n" +
                "opportunity.mm,\n" +
                "opportunity.total_mm,\n" +
                "opportunity.start_date,\n" +
                "opportunity.end_date,\n" +
                "opportunity.duration,\n" +
                "opportunity.success_rate,\n" +
                "opportunity.note,\n" +
                "opportunity.temp_pm_id,\n" +
                "opportunity.plan_pm_id,\n" +
                "opportunity.overview,\n" +
                "opportunity.status,\n" +
                "app_params.name as statusString\n" +
                "FROM\n" +
                "{h-schema}opportunity\n" +
                "INNER JOIN\n" +
                "{h-schema}app_params\n" +
                "ON\n" +
                "opportunity.status = app_params.code\n" +
                subQuery +
                " and app_params.type = ?" +
                " and opportunity.is_deleted = 0" +
                " ORDER BY opportunity.created_at DESC, opportunity.id DESC";

        Query q = entityManager.createNativeQuery(query);

        q.setParameter(1, CommonConstant.AppParams.OPPORTUNITY_STATUS);
        List<Object> objects = q.getResultList();

        for (Object o : objects) {
            Object[] ob = (Object[]) o;
            int id = Integer.valueOf(ob[0].toString());
            String oppCode = ob[1].toString();
            oppName = ob[2].toString();
            int contractType = Integer.valueOf(ob[3].toString());
            float mm = Objects.isNull(ob[4])? 0 : Float.valueOf(ob[4].toString()) ;
            float totalMm = Objects.isNull(ob[5])? 0 : Float.valueOf(ob[5].toString());
            Timestamp startDate = Objects.isNull(ob[6])? null : Timestamp.valueOf(ob[6].toString());
            Timestamp endDate = Objects.isNull(ob[7])? null : Timestamp.valueOf(ob[7].toString());
            float duration = Objects.isNull(ob[8])? 0 : Float.valueOf(ob[8].toString());
            float successRate = Objects.isNull(ob[9])? 0 : Float.valueOf(ob[9].toString());
            int tempPm = Objects.isNull(ob[11])? 0 : Integer.valueOf(ob[11].toString());;
            int planPm = Objects.isNull(ob[12])? 0 : Integer.valueOf(ob[12].toString());;
            OpportunityResponseDTO opportunityResponseDTO = new OpportunityResponseDTO(
                    id, oppCode, oppName, contractType, mm, totalMm, startDate, endDate, duration,
                    successRate, ob[10].toString(), tempPm, planPm,
                    ob[13].toString(), Integer.valueOf(ob[14].toString()), ob[15].toString()
            );

            opportunityResponseDTOS.add(opportunityResponseDTO);
        }
        return opportunityResponseDTOS;
    }

    private String createQuery(String oppName, int contractTypeId, int planPmId, int tempPmId, Timestamp fromStart, Timestamp toStart,
                               Timestamp fromEnd, Timestamp toEnd, List<String> listStatus) {
        StringBuilder sbQuery = new StringBuilder();
        if (!oppName.isEmpty()) {
            sbQuery.append("lower(opportunity.opp_name) LIKE '%")
                    .append(oppName)
                    .append("%' AND ");
        }
        if (contractTypeId != 0) {
            sbQuery.append("opportunity.contract_type_id =")
                    .append(contractTypeId)
                    .append(" AND ");
        }

        if (planPmId != 0) {
            sbQuery.append("opportunity.plan_pm_id =")
                    .append(planPmId)
                    .append(" AND ");
        }

        if (tempPmId != 0) {
            sbQuery.append("opportunity.temp_pm_id =")
                    .append(tempPmId)
                    .append(" AND ");
        }

        if (Objects.isNull(fromStart)) {
            if (!Objects.isNull(toStart)) {
                sbQuery.append(" opportunity.start_date < '")
                        .append(toStart.toString())
                        .append("'")
                        .append(" AND ");
            }
        } else {
            if (Objects.isNull(toStart)) {
                sbQuery.append(" opportunity.start_date > '")
                        .append(fromStart.toString())
                        .append("'")
                        .append(" AND ");
            } else {
                sbQuery.append(" (opportunity.start_date > '")
                        .append(fromStart.toString())
                        .append("' and opportunity.start_date < '")
                        .append(toStart.toString())
                        .append("')")
                        .append(" AND ");
            }
        }

        if (Objects.isNull(fromEnd)) {
            if (!Objects.isNull(toEnd)) {
                sbQuery.append(" opportunity.end_date < '")
                        .append(toEnd.toString())
                        .append("'")
                        .append(" AND ");
            }
        } else {
            if (Objects.isNull(toEnd)) {
                sbQuery.append(" opportunity.end_date > '")
                        .append(fromEnd.toString())
                        .append("'")
                        .append(" AND ");
            } else {
                sbQuery.append(" (opportunity.end_date > '")
                        .append(fromEnd.toString())
                        .append("' and opportunity.end_date < '")
                        .append(toEnd.toString())
                        .append("')")
                        .append(" AND ");
            }
        }
        if (Objects.isNull(fromEnd) && Objects.isNull(toEnd) && Objects.isNull(fromStart) && Objects.isNull(toStart)) {
            sbQuery.append("1=1 AND");
        }

        if (!listStatus.isEmpty()) {
            String queryCondition = " opportunity.status in (";
            for (String s : listStatus) {
                queryCondition += "'" + s + "',";
            }
            sbQuery.append(queryCondition.substring(0, queryCondition.length() - 1) + ")");
        } else {
            sbQuery.append(" 1=1");
        }

        return sbQuery.toString();
    }
}
