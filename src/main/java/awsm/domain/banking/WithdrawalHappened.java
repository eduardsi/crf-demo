package awsm.domain.banking;

import awsm.domain.core.DomainEvent;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class WithdrawalHappened implements DomainEvent {

  private final String iban;
  private final String txUid;
}
