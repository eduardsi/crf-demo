package lightweight4j.features.administration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Entity(name = "Members")
class Administrator {

    private static final Logger log = LoggerFactory.getLogger(Administrator.class);

    @Id
    private String id = UUID.randomUUID().toString();

    @OneToMany(cascade = CascadeType.ALL)
    private Collection<Permission> permissions = new ArrayList<>();

    public void grant(Permission permission) {
        log.info("Granting {} permission to do {}!", id, permission.operation());
        permissions.add(permission);
    }

}
