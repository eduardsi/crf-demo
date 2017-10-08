package net.sizovs.crf.services.permissions;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface Permissions extends org.springframework.data.repository.Repository<Permission, String> {

    long countByNameStringIgnoreCase(String name);

    Optional<Permission> findOne(String id);

    void save(Permission permission);


}
