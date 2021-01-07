package awsm.domain.crm;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Customer {

    @Id
    private String personalId;

    private String firstName;

    private String lastName;

    private String email;

    public Customer(String personalId, String firstName, String lastName, UniqueEmail email) {
        this.personalId = personalId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email.toString();
    }

    private Customer() {
    }

    public void register(CustomerRepository repo) {
        repo.save(this);
    }
}
