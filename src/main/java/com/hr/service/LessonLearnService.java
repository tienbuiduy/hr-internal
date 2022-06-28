package com.hr.service;

import com.hr.model.dto.request.AllocationRequestDTO;
import com.hr.model.dto.request.LessonLearnSearchRequestDTO;
import com.hr.model.dto.response.LessonLearnResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface LessonLearnService {
    List<LessonLearnResponseDTO> list();

    void save(AllocationRequestDTO allocationRequestDTO);

    List<LessonLearnResponseDTO> search(LessonLearnSearchRequestDTO lessonLearnSearchRequestDTO);


    String importLessonLearn(MultipartFile file) throws IOException;

    void delete(Integer id, int id1);


    String checkConditionToImportLessonLearn();
}
