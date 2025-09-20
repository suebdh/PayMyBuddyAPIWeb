package com.openclassrooms.PayMyBuddyAPIWeb.repository;

import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository Spring Data JPA pour l'entité {AppUser}.
 * <p>
 * Fournit des méthodes pour accéder aux utilisateurs dans la base de données.
 * Hérite de {JpaRepository}, offrant des opérations CRUD standard,
 * ainsi que des méthodes personnalisées pour retrouver un utilisateur par email ou par nom d'utilisateur.
 * </p>
 *
 * <p>Méthodes principales :</p>
 * <ul>
 *     <li> #findByEmail(String) : recherche un utilisateur par son email.</li>
 *     <li> #findByUserName(String) : recherche un utilisateur par son nom d'utilisateur.</li>
 * </ul>
 */
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {

    /**
     * Recherche un utilisateur par son adresse email.
     *
     * @param email l'email de l'utilisateur à rechercher
     * @return un Optional contenant l'utilisateur si trouvé, sinon vide
     */
    Optional<AppUser> findByEmail(String email);

    /**
     * Recherche un utilisateur par son nom d'utilisateur.
     *
     * @param userName le nom d'utilisateur à rechercher
     * @return un Optional contenant l'utilisateur si trouvé, sinon vide
     */
    Optional<AppUser> findByUserName(String userName);
}
