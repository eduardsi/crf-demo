package awsm.application.banking.domain;

import awsm.application.banking.ApplyForBankAccount;
import awsm.application.banking.ApplyForBankAccount.Response;
import awsm.application.registration.domain.CustomerHashId;
import awsm.infrastructure.middleware.impl.execution.Executor;
import org.springframework.stereotype.Component;

@Component
class ApplyForBankAccountExecutor implements Executor<ApplyForBankAccount, Response> {

  @Override
  public Response execute(ApplyForBankAccount cmd) {
    var customerId = new CustomerHashId(cmd.customerHashId).unhash();
    System.out.println(customerId + " has applied for account with currency " + cmd.currency);
    return null;
  }
}
