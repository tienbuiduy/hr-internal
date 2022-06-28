package com.hr.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "lesson_learn")
public class LessonLearn extends AbstractAuditEntity{
    private static final long serialVersionUID = 1L;

    private Timestamp date;
    private String description;
    private String category;
    private String impact;
    private String rootCause;
    private String correctiveAction;
    private String pic;
    private Timestamp deadLine;
    private String expectResult;
    private String actualResult;
    private String status;
    private String opinion;
    private String otherNotes;
    private String problemSource;
    private String longTermSolution;
    private String projectName;
    private int projectId;
    private String division;
    private int isDeleted = 0 ;

    public LessonLearn() {
    }

    @Basic
    @Column(name = "date")
    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "category")
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Basic
    @Column(name = "impact")
    public String getImpact() {
        return impact;
    }

    public void setImpact(String impact) {
        this.impact = impact;
    }

    @Basic
    @Column(name = "root_cause")
    public String getRootCause() {
        return rootCause;
    }

    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }

    @Basic
    @Column(name = "corrective_action")
    public String getCorrectiveAction() {
        return correctiveAction;
    }

    public void setCorrectiveAction(String correctiveAction) {
        this.correctiveAction = correctiveAction;
    }

    @Basic
    @Column(name = "pic")
    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    @Basic
    @Column(name = "dead_line")
    public Timestamp getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(Timestamp deadLine) {
        this.deadLine = deadLine;
    }

    @Basic
    @Column(name = "expect_result")
    public String getExpectResult() {
        return expectResult;
    }

    public void setExpectResult(String expectResult) {
        this.expectResult = expectResult;
    }

    @Basic
    @Column(name = "actual_result")
    public String getActualResult() {
        return actualResult;
    }

    public void setActualResult(String actualResult) {
        this.actualResult = actualResult;
    }

    @Basic
    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Basic
    @Column(name = "opinion")
    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    @Basic
    @Column(name = "other_notes")
    public String getOtherNotes() {
        return otherNotes;
    }

    public void setOtherNotes(String otherNote) {
        this.otherNotes = otherNote;
    }

    @Basic
    @Column(name = "problem_source")
    public String getProblemSource() {
        return problemSource;
    }

    public void setProblemSource(String problemSource) {
        this.problemSource = problemSource;
    }

    @Basic
    @Column(name = "long_term_solution")
    public String getLongTermSolution() {
        return longTermSolution;
    }

    public void setLongTermSolution(String longTermSolution) {
        this.longTermSolution = longTermSolution;
    }

    @Basic
    @Column(name = "project_name")
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Basic
    @Column(name = "project_id")
    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    @Basic
    @Column(name = "division")
    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    @Basic
    @Column(name = "is_deleted")
    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}
