package awsm.domain.bonus;

import awsm.domain.banking.account.BankAccountCreated;
import awsm.infrastructure.middleware.DomainEventListener;
import org.springframework.stereotype.Component;

@Component
public class SendBonusOfferOnBankAccountCreated implements DomainEventListener<BankAccountCreated> {

  @Override
  public void invoke(BankAccountCreated event) {

  }

}
