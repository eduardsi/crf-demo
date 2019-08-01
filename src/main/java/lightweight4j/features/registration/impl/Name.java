package lightweight4j.features.registration.impl;

import javax.persistence.Embeddable;

import static java.util.Objects.requireNonNull;

@Embeddable
class Name {

    String firstOne;
    String lastOne;

    Name(String firstOne, String lastOne) {
        this.firstOne = requireNonNull(firstOne, "First name cannot be null");
        this.lastOne = requireNonNull(lastOne, "Last name cannot be null");
    }

    private Name() {
    }
}
