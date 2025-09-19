package com.openclassrooms.PayMyBuddyAPIWeb.repository;

import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppTransaction;
import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppTransactionRepository extends JpaRepository<AppTransaction, Integer> {

    // récupère toutes les transactions envoyées ou reçues par un utilisateur, paginées
    Page<AppTransaction> findBySenderOrReceiver(AppUser sender, AppUser receiver, Pageable pageable);

    // compte combien de transactions pour un utilisateur (utile pour connaître le nombre de pages)
    int countBySenderOrReceiver(AppUser sender, AppUser receiver);
}
