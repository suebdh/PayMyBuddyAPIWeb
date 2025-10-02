package com.openclassrooms.PayMyBuddyAPIWeb.exception;

/**
 * Exception levée lorsqu'une tentative d'inscription ou de mise à jour
 * utilise un email déjà existant dans la base de données.
 */
public class EmailAlreadyUsedException extends RuntimeException {
    /**
     * Constructeur avec message d'erreur.
     *
     * @param message message décrivant l'erreur
     */
    public EmailAlreadyUsedException(String message) {
        super(message);
    }
}