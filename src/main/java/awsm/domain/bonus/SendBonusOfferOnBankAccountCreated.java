package awsm.domain.bonus;

import awsm.domain.banking.BankAccountCreated;
import awsm.infrastructure.middleware.SideEffect;
import org.springframework.stereotype.Component;

@Component
public class SendBonusOfferOnBankAccountCreated implements SideEffect<BankAccountCreated> {

  @Override
  public void invoke(BankAccountCreated event) {

  }

}
