package com.hr.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hr.constant.CommonConstant;
import com.hr.model.GoogleUser;
import com.hr.model.Project;
import com.hr.model.User;
import com.hr.model.dto.request.ProjectRequestDTO;
import com.hr.model.dto.request.ProjectSearchRequestDTO;
import com.hr.model.dto.response.ProjectResponseDTO;
import com.hr.repository.ProjectRepository;
import com.hr.repository.UserRepository;
import com.hr.service.ProjectService;
import com.hr.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackOn = Exception.class)
public class ProjectServiceImpl implements ProjectService {
    private ModelMapper modelMapper;

    private ProjectRepository projectRepository;

    private UserRepository userRepository;

    private UserService userService;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, UserService userService, ModelMapper modelMapper) {
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public Project createOrUpdate(ProjectRequestDTO projectRequestDTO) {
        return projectRepository.save(toProject(projectRequestDTO));
    }

    @Override
    public List<String> getListRank() {
        return projectRepository.getListRank();
    }

    @Override
    public List<ProjectResponseDTO> list() {
        List<Project> projects = getActiveProject();
        return projects.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    public List<Project> getActiveProject() {
        return projectRepository.findActiveProject();
    }

    @Override
    public List<ProjectResponseDTO> listProjectsOfLoginUser(OAuth2AuthenticationToken authentication) {
        OAuth2AuthenticatedPrincipal oauthUser = authentication.getPrincipal();
        ObjectMapper objectMap = new ObjectMapper();
        GoogleUser googleUser = objectMap.convertValue(oauthUser.getAttributes(), GoogleUser.class);
        User user = userRepository.findByEmailAndIsDeleted(googleUser.getEmail(), CommonConstant.NOT_DELETED);

        List<Project> projects;
        if(userService.getListRoleCodeOfUser(user).contains(CommonConstant.RoleCode.DIVISION_MANAGER) ||
                userService.getListRoleCodeOfUser(user).contains(CommonConstant.RoleCode.PMO)){
            projects = projectRepository.findActiveProject();
        }
        else {
            projects = projectRepository.findAllByPmId(user.getId());
        }
        return projects.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public void delete(int id, int loginUserId) {
        projectRepository.deleteProject(id, loginUserId);
    }

    @Override
    public List<ProjectResponseDTO> search(ProjectSearchRequestDTO projectSearchRequestDTO) {
        String projectName = projectSearchRequestDTO.getProjectName().toLowerCase().trim();
        int contractTypeId = projectSearchRequestDTO.getContractTypeId();
        String rankCode = projectSearchRequestDTO.getRankId();
        int customerId = projectSearchRequestDTO.getCustomerId();
        int pmId = projectSearchRequestDTO.getPmId();
        String status = projectSearchRequestDTO.getStatus();
        String technicalSkill = projectSearchRequestDTO.getTechnicalSkill().toLowerCase().trim();

        List<Project> projectSearchResult = projectRepository.search(projectName, contractTypeId, rankCode, customerId, pmId, status, technicalSkill);

        List<ProjectResponseDTO> projectResponseDTOs = new ArrayList<>();
        for (Project p : projectSearchResult) {
            projectResponseDTOs.add(toResponseDTO(p));
        }
        return projectResponseDTOs;
    }

    @Override
    public Integer getLatestProjectId(){
        return projectRepository.findFirstByOrderByIdDesc().getId();
    }

    @Override
    public Project getProject(int projectId) {
        Project project=projectRepository.findById( projectId);
        if(Objects.isNull(project))
        {
            throw new NullPointerException();
        }
        return project;
    }

    public Project toProject(ProjectRequestDTO projectRequestDTO) {
        Project project = modelMapper.map(projectRequestDTO, Project.class);

        int loginUserId = userService.getLoginUserId();
        if(projectRequestDTO.getId() == 0){
            project.setCreatedBy(loginUserId);
        } else {
            Project projectOldData = projectRepository.findById(projectRequestDTO.getId());
            project.setCreatedBy(projectOldData.getCreatedBy());
        }
        project.setUpdatedBy(loginUserId);
        return project;
    }

    private ProjectResponseDTO toResponseDTO(Project project) {
        ProjectResponseDTO projectResponseDTO = modelMapper.map(project, ProjectResponseDTO.class);
        projectResponseDTO.setRankName(projectRepository.getRankNameByRankCode(project.getRank(), CommonConstant.AppParams.PROJECT_RANK_TYPE));
        return projectResponseDTO;
    }
}
