package com.qli.contactapi.repo;

import com.qli.contactapi.domain.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepo extends JpaRepository<Contact, String> {
    Optional<Contact> findById(String id);
    List<Contact> findByName(String name);
    List<Contact> findByEmail(String email);
    List<Contact> findByPhone(String phone);
    List<Contact> findByTitle(String title);
    List<Contact> findByAddress(String address);
    List<Contact> findByStatus(String status);
    List<Contact> findByPhotoUrl(String photoUrl);
}
