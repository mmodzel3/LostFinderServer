package com.github.mmodzel3.lostfinderserver.security.authentication.register;

class AccountExistsException extends Exception {
    private static final String ACCOUNT_EXISTS_MSG = "Account with specific email exists";

    AccountExistsException() {
        super(ACCOUNT_EXISTS_MSG);
    }
}
