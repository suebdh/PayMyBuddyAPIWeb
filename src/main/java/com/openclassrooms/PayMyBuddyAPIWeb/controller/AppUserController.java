package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.AppUserDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.dto.RegisterDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.service.AppUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    @GetMapping
    public List<AppUserDTO> getAllUsers() {
        return appUserService.getAllUsers();
    }

    @GetMapping("id/{id}")
    public ResponseEntity<AppUserDTO> getUserById(@PathVariable int id) {
        return appUserService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("userName/{userName}")
    public ResponseEntity<AppUserDTO> getUserByUserName(@PathVariable String userName){
        return appUserService.getUserByUserName(userName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("email/{email}")
    public ResponseEntity<AppUserDTO> getUserByEmail(@PathVariable String email) {
        return appUserService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@Valid @RequestBody RegisterDTO registerDTO){
        return ResponseEntity.ok().build();
    }


}
