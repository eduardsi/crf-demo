package lightweight4j.features.membership.impl;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import static com.google.common.base.Preconditions.checkArgument;

@Embeddable
public class Email {

    @Transient
    @Autowired
    private EmailBlacklist blacklist;

    private String email;

    public Email(String email, EmailBlacklist blacklist) {
        checkArgument(email != null && !email.isEmpty() && !email.isBlank() && email.contains("."),
            "Email %s is not valid", email);
        this.email = email;
        this.blacklist = blacklist;
    }

    private Email() {
    }

    public boolean isBlacklisted() {
        return blacklist.contains(this);
    }

    @Override
    public String toString() {
        return email;
    }

}
