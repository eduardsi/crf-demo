package awsm.domain.banking.aml;

import awsm.domain.core.Amount;
import awsm.domain.core.DomainEntity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
class TransactionForReview implements DomainEntity<TransactionForReview> {

  @Id @GeneratedValue private Long id;

  private String iban;
  private String txId;
  private Amount withdrawn;

  private TransactionForReview() {}

  TransactionForReview(String txId, Amount withdrawn, String iban) {
    this.txId = txId;
    this.withdrawn = withdrawn;
    this.iban = iban;
  }
}
