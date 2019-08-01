package lightweight4j.features.registration.impl;

class EmailIsBlacklistedException extends RuntimeException {

    private Email email;

    EmailIsBlacklistedException(Email email) {

        this.email = email;
    }

    @Override
    public String getMessage() {
        return "Email " + email + " is in the blacklist";
    }
}
