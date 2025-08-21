package com.openclassrooms.PayMyBuddyAPIWeb.service;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.AppUserDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppUser;
import com.openclassrooms.PayMyBuddyAPIWeb.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    public List<AppUserDTO> getAllUsers(){
        return ((List<AppUser>) appUserRepository.findAll())
                .stream()  // Transforme la liste en Stream
                .map(this::convertToDTO) // Convertit chaque AppUser en AppUserDTO
                .collect(Collectors.toList()); // Re-transforme en List
    }

    public Optional<AppUserDTO> getUserById(int id){
        return appUserRepository.findById(id)
                .map(this::convertToDTO);
    }

    public Optional<AppUserDTO> getUserByUserName(String name){
        return appUserRepository.findByUserName(name)
                .map(this::convertToDTO);
    }

    public Optional<AppUserDTO> getUserByEmail(String email){
        return appUserRepository.findByEmail(email)
                .map(this::convertToDTO);
    }

    public AppUserDTO createUser(AppUserDTO appUserDTO){
        return convertToDTO(appUserRepository.save(convertToEntity(appUserDTO)));
    }

    public AppUserDTO convertToDTO(AppUser appUser){
        return new AppUserDTO(
                appUser.getUserId(),
                appUser.getUserName(),
                appUser.getEmail(),
                appUser.getPassword(),
                appUser.getBalance(),
                appUser.getUserCreatedAt()
        );

    }

    public AppUser convertToEntity(AppUserDTO appUserDTO){
        return new AppUser(
                appUserDTO.getUserId(),
                appUserDTO.getUserName(),
                appUserDTO.getEmail(),
                appUserDTO.getPassword(),
                appUserDTO.getBalance(),
                appUserDTO.getUserCreatedAt(),
                null
        );

    }
}
