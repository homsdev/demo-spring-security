package com.homsdev.springsecuritybasic.domain.repository;

import com.homsdev.springsecuritybasic.domain.Contact;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends CrudRepository<Contact, Long> {
}
