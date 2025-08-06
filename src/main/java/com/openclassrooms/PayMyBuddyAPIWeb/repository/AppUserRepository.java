package com.openclassrooms.PayMyBuddyAPIWeb.repository;

import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends CrudRepository<AppUser, Integer> {

    public Optional<AppUser> findByEmail(String email);

    public Optional<AppUser> findByUserName(String userName);
}
