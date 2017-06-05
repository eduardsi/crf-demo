package net.sizovs.crf.services.membership;

import com.google.common.collect.ImmutableList;
import net.sizovs.crf.services.permissions.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private String email;

    public Member(String email) {
        this.email = email;
    }

    private Member() {
    }

    public void grantDefaultPermissions() {
        grantPermission(new Permission("DEFAULT1"));
        grantPermission(new Permission("DEFAULT2"));
        grantPermission(new Permission("DEFAULT3"));
    }

    public void grantPermission(Permission permission) {
        log.info("Granting {} to {}!", permission.name(), id);
        permissions.add(permission);
    }

    public Collection<Permission> permissions() {
        return ImmutableList.copyOf(permissions);
    }

    public String id() {
        return id;
    }
}
