package com.openclassrooms.PayMyBuddyAPIWeb.exception;

/**
 * Exception levée lorsque aucun utilisateur authentifié n'est trouvé dans le contexte de sécurité.
 */
public class AuthenticatedUserNotFoundException extends RuntimeException {
    /**
     * Constructeur avec message d'erreur.
     *
     * @param message message décrivant l'erreur
     */
    public AuthenticatedUserNotFoundException(String message) {
        super(message);
    }
}
