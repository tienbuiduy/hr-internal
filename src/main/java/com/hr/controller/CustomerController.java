package com.hr.controller;

import com.hr.constant.CommonConstant;
import com.hr.model.Customer;
import com.hr.model.User;
import com.hr.model.dto.request.CustomerRequestDTO;
import com.hr.model.dto.request.CustomerSearchRequestDTO;
import com.hr.model.dto.response.AppParamsAllAttributesResponseDTO;
import com.hr.model.dto.response.CustomerResponseDTO;
import com.hr.service.AppParamsService;
import com.hr.service.CustomerService;
import com.hr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private AppParamsService appParamsService;

    @Autowired
    private UserService userService;

    @GetMapping
    public List<CustomerResponseDTO> list() {
        return customerService.list();
    }

    @GetMapping("/appParams")
    public List<AppParamsAllAttributesResponseDTO> appParams() {
        return appParamsService.listAllAttributes();
    }

    @GetMapping("/generateCode")
    public String customerCode() {
        return customerService.generateCode();
    }

    @SuppressWarnings("static-access")
    @PostMapping
    public ResponseEntity<CustomerRequestDTO> createOrUpdate(@RequestBody CustomerRequestDTO customerRequestDTO) {
        try {
            customerService.createOrUpdate(customerRequestDTO);
            return new ResponseEntity<Customer>(HttpStatus.OK).ok(customerRequestDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        User loginUser = userService.getLoginUser();
        List<String> userRoleList = userService.getListRoleCodeOfUser(loginUser);
        if (userRoleList.contains(CommonConstant.RoleCode.DIVISION_MANAGER) || userRoleList.contains(CommonConstant.RoleCode.PMO) || userRoleList.contains(CommonConstant.RoleCode.PROJECT_MANAGER)) {
            try {
                customerService.delete(id, loginUser.getId());
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/search")
    public List<CustomerResponseDTO> search(@RequestBody CustomerSearchRequestDTO customerSearchRequestDTO) {
        return customerService.search(customerSearchRequestDTO);
    }
}
