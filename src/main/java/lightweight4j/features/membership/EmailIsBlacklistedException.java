package lightweight4j.features.membership;

class EmailIsBlacklistedException extends RuntimeException {

    private Email email;

    public EmailIsBlacklistedException(Email email) {

        this.email = email;
    }

    @Override
    public String getMessage() {
        return "Email " + email + " is in the blacklist";
    }
}
