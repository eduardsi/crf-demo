package lightweight4j.features.registration.impl;

import javax.persistence.Embeddable;

@Embeddable
class Name {

    String firstOne;
    String lastOne;

    Name(String firstOne, String lastOne) {
        this.firstOne = firstOne;
        this.lastOne = lastOne;
    }

    private Name() {
    }
}
