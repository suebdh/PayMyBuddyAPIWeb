package com.openclassrooms.PayMyBuddyAPIWeb.repository;

import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppTransactionRepository extends JpaRepository <AppTransaction, Integer> {
    //Pas besoin d'ajouter des méthodes, besoin pour le save uniquement pour l'instant
}
