package lightweight4j.lib.domain;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public abstract class AggregateRoot extends DomainEntity {

    @Version
    private Long version;


}

