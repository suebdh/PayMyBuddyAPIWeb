package com.openclassrooms.PayMyBuddyAPIWeb.exception;

/**
 * Exception levée lorsqu'un utilisateur recherché n'existe pas
 * dans la base de données.
 */
public class UserNotFoundException extends RuntimeException {
    /**
     * Constructeur avec message d'erreur.
     *
     * @param message message décrivant l'erreur
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}