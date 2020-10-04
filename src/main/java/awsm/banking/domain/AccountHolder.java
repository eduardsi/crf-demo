package awsm.banking.domain;

import javax.persistence.Embeddable;

@Embeddable
public class AccountHolder {

    private String firstName;
    private String lastName;

    AccountHolder(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    private AccountHolder() {
    }

    public String name() {
        return firstName + " " + lastName;
    }

}
