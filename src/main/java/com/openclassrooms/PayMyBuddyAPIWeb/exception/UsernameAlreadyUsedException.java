package com.openclassrooms.PayMyBuddyAPIWeb.exception;

/**
 * Exception levée lorsqu'une tentative d'inscription ou de mise à jour
 * utilise un nom d'utilisateur déjà existant dans la base de données.
 */
public class UsernameAlreadyUsedException  extends RuntimeException{
    /**
     * Constructeur avec message d'erreur.
     *
     * @param message message décrivant l'erreur
     */
    public UsernameAlreadyUsedException(String message) {
        super(message);
    }
}
