package awsm.domain.administration;

import awsm.infra.hibernate.HibernateConstructor;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
public class Administrator  {

  private static final Logger log = LoggerFactory.getLogger(Administrator.class);

  @Id
  private long id;

  @OneToMany(cascade = CascadeType.ALL)
  private Collection<Permission> permissions = new ArrayList<>();

  public Administrator(long memberId) {
    this.id = memberId;
  }


  @HibernateConstructor
  private Administrator() {
  }

  public void grant(Permission permission) {
    log.info("Granting {} permission to do {}!", id, permission.operation());
    permissions.add(permission);
  }

}
