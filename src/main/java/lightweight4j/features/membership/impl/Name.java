package lightweight4j.features.membership.impl;

import lightweight4j.lib.hibernate.HibernateConstructor;

import javax.persistence.Embeddable;

@Embeddable
public class Name {

    private String firstOne;
    private String lastOne;

    public Name(String firstOne, String lastOne) {
        this.firstOne = firstOne;
        this.lastOne = lastOne;
    }

    @HibernateConstructor
    private Name() {
    }
}
