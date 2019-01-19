package net.sizovs.crf.services.membership;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
public class Email {

    @Transient
    @Autowired
    private EmailBlacklist blacklist;

    private String email;

    public Email(String email, EmailBlacklist blacklist) {
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
