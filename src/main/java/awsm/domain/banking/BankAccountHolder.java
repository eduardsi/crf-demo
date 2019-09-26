package awsm.domain.banking;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
class BankAccountHolder {

  @Id
  @Nullable
  @GeneratedValue
  private Long id;

}
