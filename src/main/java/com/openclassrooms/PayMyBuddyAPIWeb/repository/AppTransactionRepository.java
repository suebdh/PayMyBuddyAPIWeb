package com.openclassrooms.PayMyBuddyAPIWeb.repository;

import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppTransaction;
import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository Spring Data JPA pour l'entité {AppTransaction}.
 * <p>
 * Fournit des méthodes pour accéder aux transactions dans la base de données,
 * incluant les transactions envoyées et reçues par un utilisateur.
 * Utilise la pagination pour récupérer les résultats par pages.
 * </p>
 *
 * <p>Méthodes principales :</p>
 * <ul>
 *     <li> #findBySenderOrReceiver(AppUser, AppUser, Pageable) : récupère toutes les transactions
 *         envoyées ou reçues par un utilisateur, avec pagination.</li>
 *     <li> #countBySenderOrReceiver(AppUser, AppUser) : compte le nombre total de transactions
 *         pour un utilisateur (utile pour le calcul du nombre de pages).</li>
 * </ul>
 *
 * <p>Hérite de JpaRepository, fournissant des méthodes CRUD standard.</p>
 */
@Repository
public interface AppTransactionRepository extends JpaRepository<AppTransaction, Integer> {

    /**
     * Récupère toutes les transactions telles que l'utilisateur est soit l'expéditeur,
     * soit le destinataire, avec pagination.
     *
     * @param sender   l'utilisateur expéditeur
     * @param receiver l'utilisateur destinataire
     * @param pageable objet de pagination (page, taille, tri)
     * @return une page de AppTransaction correspondant à l'utilisateur
     */
    Page<AppTransaction> findBySenderOrReceiver(AppUser sender, AppUser receiver, Pageable pageable);


    /**
     * Compte le nombre total de transactions où l'utilisateur est soit l'expéditeur,
     * soit le destinataire.
     *
     * @param sender   l'utilisateur expéditeur
     * @param receiver l'utilisateur destinataire
     * @return le nombre total de transactions pour l'utilisateur
     */
    int countBySenderOrReceiver(AppUser sender, AppUser receiver);
    // Cette méthode est utile pour connaître le nombre de pages
}
