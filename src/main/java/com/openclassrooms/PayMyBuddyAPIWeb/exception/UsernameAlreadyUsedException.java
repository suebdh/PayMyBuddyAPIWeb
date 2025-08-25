package com.openclassrooms.PayMyBuddyAPIWeb.exception;

public class UsernameAlreadyUsedException  extends RuntimeException{
    public UsernameAlreadyUsedException(String message) {
        super(message);
    }
}
