package com.hr.service.impl;

import com.hr.model.Customer;
import com.hr.model.dto.request.CustomerRequestDTO;
import com.hr.model.dto.request.CustomerSearchRequestDTO;
import com.hr.model.dto.response.CustomerResponseDTO;
import com.hr.repository.CustomerRepository;
import com.hr.repository.UserRepository;
import com.hr.service.CustomerService;
import com.hr.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackOn = Exception.class)
public class CustomerServiceImpl implements CustomerService {

    private final String DEFAULT_CUSTOMER_CODE = "C000001";

    private CustomerRepository customerRepository;

    private ModelMapper modelMapper;

    private UserRepository userRepository;

    private UserService userService;

    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper, UserRepository userRepository, UserService userService) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public List<CustomerResponseDTO> list() {
        List<Customer> customers = getActiveCustomer();
        return customers.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public Customer createOrUpdate(CustomerRequestDTO customerRequestDTO) {
        return customerRepository.save(toCustomer(customerRequestDTO));
    }

    @Override
    public synchronized String generateCode() {
        Customer lastCustomer = customerRepository.findFirstByOrderByIdDesc();
        if (lastCustomer == null) {
            return DEFAULT_CUSTOMER_CODE;
        }
        String code = "C" + String.format("%06d", lastCustomer.getId() + 1);
        return code;
    }

    @Override
    public List<Customer> getActiveCustomer() {
        return customerRepository.findActiveCustomer();
    }

    @Override
    public void delete(int id, int loginUserId) {
        customerRepository.deleteCustomer(id, loginUserId);
    }

    @Override
    public List<CustomerResponseDTO> search(CustomerSearchRequestDTO customerSearchRequestDTO) {
        String customerCode = customerSearchRequestDTO.getCustomerCode().toLowerCase().trim();
        String customerName = customerSearchRequestDTO.getCustomerName().toLowerCase().trim();
        String countryCode = customerSearchRequestDTO.getCountryCode().toLowerCase().trim();
        String rank = customerSearchRequestDTO.getRank().toLowerCase().trim();

        List<Customer> customerList = customerRepository.search(customerCode, customerName, countryCode, rank);
        List<CustomerResponseDTO> customerResponseDTOs = new ArrayList<>();
        for (Customer c : customerList) {
            customerResponseDTOs.add(toResponseDTO(c));
        }
        return customerResponseDTOs;
    }

    @Override
    public CustomerResponseDTO toResponseDTO(Customer customer) {
        CustomerResponseDTO customerResponseDTO = modelMapper.map(customer, CustomerResponseDTO.class);
        return customerResponseDTO;
    }

    private Customer toCustomer(CustomerRequestDTO customerRequestDTO) {
        Customer customer = modelMapper.map(customerRequestDTO, Customer.class);

        int loginUserId = userService.getLoginUserId();
        if(customerRequestDTO.getId() == 0){
            customer.setCreatedBy(loginUserId);
        } else {
            Customer customerOldData = customerRepository.findById(customerRequestDTO.getId());
            customer.setCreatedBy(customerOldData.getCreatedBy());
        }
        customer.setUpdatedBy(loginUserId);
        return customer;
    }

}
