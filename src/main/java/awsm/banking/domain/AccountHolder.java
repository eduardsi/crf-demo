package awsm.banking.domain;

import javax.persistence.Embeddable;

@Embeddable
public class AccountHolder {

    private String personalId;
    private String firstName;
    private String lastName;

    public AccountHolder(String firstName, String lastName, String personalId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalId = personalId;
    }

    private AccountHolder() {
    }

    public String name() {
        return firstName + " " + lastName;
    }

}
