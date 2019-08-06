package lightweight4j.domain.registration;

public class EmailBlacklisted extends RuntimeException {

    private Email email;

    public EmailBlacklisted(Email email) {

        this.email = email;
    }

    @Override
    public String getMessage() {
        return "Email " + email + " is in the blacklist";
    }
}
