package lightweight4j.features.administration.impl;

import lightweight4j.lib.domain.DomainEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "members")
class Administrator extends DomainEntity {

    private static final Logger log = LoggerFactory.getLogger(Administrator.class);

    @OneToMany(cascade = CascadeType.ALL)
    private Collection<Permission> permissions = new ArrayList<>();

    public void grant(Permission permission) {
        log.info("Granting {} permission to do {}!", id(), permission.operation());
        permissions.add(permission);
    }

}
