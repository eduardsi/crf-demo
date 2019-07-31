package lightweight4j.features.registration.impl;

import lightweight4j.lib.hibernate.HibernateConstructor;

import javax.persistence.Embeddable;

@Embeddable
class Name {

    public String firstOne;
    public String lastOne;

    public Name(String firstOne, String lastOne) {
        this.firstOne = firstOne;
        this.lastOne = lastOne;
    }

    @HibernateConstructor
    private Name() {
    }
}
