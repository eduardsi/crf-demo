package lightweight4j.domain.registration;

import javax.persistence.Embeddable;

import static java.util.Objects.requireNonNull;

@Embeddable
public class Name {

    String firstOne;
    String lastOne;

    public Name(String firstOne, String lastOne) {
        this.firstOne = requireNonNull(firstOne, "First name cannot be null");
        this.lastOne = requireNonNull(lastOne, "Last name cannot be null");
    }

    private Name() {
    }

    @Override
    public String toString() {
        return firstOne + " " + lastOne;
    }
}
