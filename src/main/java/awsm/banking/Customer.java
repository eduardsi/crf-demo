package awsm.banking;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Customer {

    @Id
    private String email;

    private String firstName;
    private String lastName;

    Customer(String email, String firstName, String lastName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    private Customer() {
    }

    String email() {
        return email;
    }

    public String name() {
        return firstName + " " + lastName;
    }
}
