package lightweight4j.features.membership.impl;

import com.google.common.collect.ImmutableList;
import lightweight4j.features.permissions.impl.Permission;
import lightweight4j.lib.hibernate.HibernateConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Entity
public class Member {

    private static final Logger log = LoggerFactory.getLogger(Member.class);

    @Id
    private String id = UUID.randomUUID().toString();

    @OneToMany
    private Collection<Permission> permissions = new ArrayList<>();

    @Embedded
    private Email email;

    @Embedded
    private Name name;

    public Member(Name name, Email email) {
        this.name = name;
        this.email = email;
    }

    @HibernateConstructor
    private Member() {
    }

    public void grant(Permission permission) {
        log.info("Granting {} to {}!", permission.name(), id);
        permissions.add(permission);
    }

    public Collection<Permission> permissions() {
        return ImmutableList.copyOf(permissions);
    }

    public Email email() {
        return email;
    }

    public String id() {
        return id;
    }
}
