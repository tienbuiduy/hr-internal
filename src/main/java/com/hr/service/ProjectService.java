package com.hr.service;

import com.hr.model.Project;
import com.hr.model.dto.request.ProjectRequestDTO;
import com.hr.model.dto.request.ProjectSearchRequestDTO;
import com.hr.model.dto.response.ProjectResponseDTO;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.List;

public interface ProjectService {
    Project createOrUpdate(ProjectRequestDTO projectRequestDTO);

    List<String> getListRank();

    List<ProjectResponseDTO> list();

    List<ProjectResponseDTO> listProjectsOfLoginUser(OAuth2AuthenticationToken authentication);

    void delete(int id, int loginUserId);

    List<ProjectResponseDTO> search(ProjectSearchRequestDTO projectSearchRequestDTO);

    Integer getLatestProjectId();

    Project getProject(int projectId);
}
