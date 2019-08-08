package awsm.domain.administration;

import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
public class Administrator  {

  private static final Logger log = LoggerFactory.getLogger(Administrator.class);

  @Id
  @GeneratedValue
  @Nullable
  private Long id;

  @Column(name = "MEMBER_ID", nullable = false)
  private Long memberId;

  @OneToMany(cascade = CascadeType.ALL)
  private Collection<Permission> permissions = new ArrayList<>();

  public Administrator(Long memberId) {
    this.memberId = memberId;
  }

  public void grant(Permission permission) {
    log.info("Granting {} permission to do {}!", memberId, permission.operation());
    permissions.add(permission);
  }

}
