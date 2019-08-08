package awsm.domain.administration;

import awsm.infra.hibernate.HibernateEntity;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    log.info("Granting {} permission to do {}!", memberId, permission.operation());
    permissions.add(permission);
  }

}
