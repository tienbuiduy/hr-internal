package com.hr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hr.constant.CommonConstant;
import com.hr.model.GoogleUser;
import com.hr.model.User;
import com.hr.model.dto.FunctionDTO;
import com.hr.service.FunctionService;
import com.hr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/functions")
public class FunctionController {
    @Autowired
    private FunctionService functionService;

    @Autowired
    private UserService userService;

    public FunctionController(FunctionService functionService, UserService userService) {
        this.functionService = functionService;
        this.userService = userService;
    }

    @GetMapping("/user-functions")
    public List<FunctionDTO> roles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ObjectMapper objectMap = new ObjectMapper();
        GoogleUser googleUser = objectMap.convertValue(auth.getPrincipal(), GoogleUser.class);
        User user = userService.findByEmailAndIsDeleted(googleUser.getEmail(), CommonConstant.NOT_DELETED);
        if (user != null) {
            return functionService.getFunctionByUserId(user.getId());
        } else {
            return new ArrayList<>();
        }
    }
}