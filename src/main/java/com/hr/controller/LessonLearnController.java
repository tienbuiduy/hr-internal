package com.hr.controller;

import com.hr.constant.CommonConstant;
import com.hr.model.User;
import com.hr.model.dto.request.LessonLearnSearchRequestDTO;
import com.hr.model.dto.response.LessonLearnResponseDTO;
import com.hr.service.LessonLearnService;
import com.hr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/lessonlearns")
public class LessonLearnController {
    @Autowired
    private LessonLearnService lessonLearnService;

    @Autowired
    private UserService userService;

    @RequestMapping
    public List<LessonLearnResponseDTO> list(){
        return lessonLearnService.list();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        User loginUser = userService.getLoginUser();
        List<String> userRoleList = userService.getListRoleCodeOfUser(loginUser);
        if (userRoleList.contains(CommonConstant.RoleCode.DIVISION_MANAGER) || userRoleList.contains(CommonConstant.RoleCode.PMO)) {
            try {
                lessonLearnService.delete(id, loginUser.getId());
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
    public List<LessonLearnResponseDTO> search(@Valid @RequestBody LessonLearnSearchRequestDTO lessonLearnSearchRequestDTO) {
        return lessonLearnService.search(lessonLearnSearchRequestDTO);
    }

    @PostMapping("/import")
    public ResponseEntity<?> importLessonLearn(MultipartFile file) {
        String errorMessages = lessonLearnService.checkConditionToImportLessonLearn();
        if (!errorMessages.isEmpty()) {
            return ResponseEntity.badRequest().body(errorMessages);
        }
        try {
            String message = lessonLearnService.importLessonLearn(file);
            return new ResponseEntity<>(HttpStatus.OK).ok(message);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
