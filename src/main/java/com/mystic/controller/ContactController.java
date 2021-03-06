package com.mystic.controller;

import com.mystic.model.dto.UserDTO;
import com.mystic.model.entity.Contact;
import com.mystic.model.entity.User;
import com.mystic.service.ContactService;
import com.mystic.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Putrenkov Pavlo
 */
@RestController
@AllArgsConstructor
public class ContactController {

    private final UserService userService;
    private final ContactService contactService;


    @GetMapping(value = "/contacts/get-all",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity returnAllContact() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userService.findByUsername(authentication.getName());


        ModelMapper modelMapper = new ModelMapper();

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        List<Contact> contacts = contactService.getByUserId(userDTO.getUserId());

        if (contacts.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_ACCEPTABLE)
                    .body("User does not have contacts");
        } else {
            return ResponseEntity
                    .ok(contacts);
        }

    }

    @PostMapping(value = "contacts/add")
    public Contact addContact(Contact contact) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userService.findByUsername(authentication.getName());

        contact.setUserId(user.getUserId());

        return contactService.saveContact(contact);
    }

    @PostMapping(value = "contacts/delete")
    public void deleteContact(Contact contact) {
        contactService.deleteByUserId(contact.getContactId());
    }

    @PostMapping(value = "contacts/update")
    public void updateContact(Contact contact) {

        contactService.updateContact(contact);
    }
}
