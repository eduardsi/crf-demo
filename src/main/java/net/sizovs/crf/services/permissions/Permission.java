package net.sizovs.crf.services.permissions;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Permission {

    @Id
    private String id = UUID.randomUUID().toString();

    private String name;

    public Permission(String name) {
        this.name = name;
    }

    private Permission() {
    }

    public String name() {
        return name;
    }

    public String id() {
        return id;
    }
}
