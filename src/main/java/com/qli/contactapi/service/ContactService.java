package com.qli.contactapi.service;

import com.qli.contactapi.domain.Contact;
import com.qli.contactapi.repo.ContactRepo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.qli.contactapi.constant.Constant.PHOTO_DIRECTORY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
public class ContactService {
    private final ContactRepo contactRepo;

    public ContactService(ContactRepo contactRepo) {
        this.contactRepo = contactRepo;
    }

    public Page<Contact> getAllContacts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        return contactRepo.findAll(pageable);
    }

    public Contact getContactById(String id) {
        return contactRepo.findById(id).orElseThrow( () -> new RuntimeException("Contact not found"));
    }

    public Contact createContact(Contact contact) {
        return contactRepo.save(contact);
    }

    public void deleteContact(String id) {
        contactRepo.deleteById(id);
    }

    public String uploadPhoto(String id, MultipartFile file) {
        log.info("Uploading photo for contact with id: {}", id);
        Optional<Contact> contact = contactRepo.findById(id);

        String photoUrl = null;
        photoUrl = photoFunction.apply(id, file);
        if (contact.isPresent() ) {
            Contact contactObj = contact.get();
            contactObj.setPhotoUrl(photoUrl);
            contactRepo.save(contactObj);
        }

        return photoUrl;
    }

    private final Function<String,String> fileExtension = filename -> Optional.of(filename)
            .filter(f -> f.contains("."))
            .map(f -> "." + f.substring(filename.lastIndexOf(".") + 1))
            .orElse(".png");
    private final BiFunction<String, MultipartFile, String> photoFunction = (id, image) -> {
        String filename = id + fileExtension.apply(image.getOriginalFilename());
        try {
            Path fileStorageLocation = Paths.get(PHOTO_DIRECTORY).toAbsolutePath().normalize();
            if (!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
            }
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(filename), REPLACE_EXISTING);
            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/contacts/image/" + filename)
                    .toUriString();
        }
        catch (Exception e) {
            log.error("Could not create the directory where the uploaded files will be stored.", e);
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", e);
        }
    };
}
