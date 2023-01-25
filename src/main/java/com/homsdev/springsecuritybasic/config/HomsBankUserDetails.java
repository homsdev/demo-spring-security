package com.homsdev.springsecuritybasic.config;

import com.homsdev.springsecuritybasic.domain.Customer;
import com.homsdev.springsecuritybasic.domain.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//@Service
public class HomsBankUserDetails implements UserDetailsService {

    CustomerRepository customerRepository;

    @Autowired
    public HomsBankUserDetails(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> user = customerRepository.findByEmail(username);
        String userName, password = null;
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.isPresent()) {
            Customer customer = user.get();
            userName = customer.getEmail();
            password = customer.getPwd();
            authorities.add(new SimpleGrantedAuthority(customer.getRole()));
        } else {
            throw new UsernameNotFoundException("User not found");
        }
        return new User(userName, password, authorities);
    }
}
