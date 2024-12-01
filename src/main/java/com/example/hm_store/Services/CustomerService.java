package com.example.hm_store.Services;

import com.example.hm_store.entity.Customer;
import com.example.hm_store.repo.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }

    public void save(Customer customer){
        customerRepository.save(customer);
    }
}
