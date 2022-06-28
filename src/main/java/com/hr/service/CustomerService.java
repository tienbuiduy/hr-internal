package com.hr.service;

import com.hr.model.Customer;
import com.hr.model.dto.request.CustomerRequestDTO;
import com.hr.model.dto.request.CustomerSearchRequestDTO;
import com.hr.model.dto.response.CustomerResponseDTO;

import java.util.List;

public interface CustomerService {
    List<CustomerResponseDTO> list();

    String generateCode();

    Customer createOrUpdate(CustomerRequestDTO customerRequestDTO) throws Exception;

    List<CustomerResponseDTO> search(CustomerSearchRequestDTO customerSearchRequestDTO);

    List<Customer> getActiveCustomer();

    void delete(int id, int loginUserId);

    CustomerResponseDTO toResponseDTO(Customer customer);
}
