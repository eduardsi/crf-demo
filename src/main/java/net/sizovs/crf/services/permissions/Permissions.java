package net.sizovs.crf.services.permissions;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface Permissions extends CrudRepository<Permission, String> {

    long countByNameStringIgnoreCase(String name);

}
