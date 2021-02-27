package awsm.domain.banking;

import awsm.domain.core.Data;
import awsm.domain.core.DomainEvent;

public class WithdrawalHappened extends Data implements DomainEvent {

  private final String iban;
  private final String txUid;

  public WithdrawalHappened(String iban, String txUid) {
    this.iban = iban;
    this.txUid = txUid;
  }

  String iban() {
    return iban;
  }

  String txUid() {
    return txUid;
  }
}
