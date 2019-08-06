package lightweight4j.domain.administration;

import lightweight4j.infra.hibernate.HibernateEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
public class Administrator extends HibernateEntity {

    private static final Logger log = LoggerFactory.getLogger(Administrator.class);

    @Column(name = "MEMBER_ID", nullable = false)
    private Long memberId;

    @OneToMany(cascade = CascadeType.ALL)
    private Collection<Permission> permissions = new ArrayList<>();

    Administrator(Long memberId) {
        this.memberId = memberId;
    }

    public void grant(Permission permission) {
        log.info("Granting {} permission to do {}!", id(), permission.operation());
        permissions.add(permission);
    }

}
