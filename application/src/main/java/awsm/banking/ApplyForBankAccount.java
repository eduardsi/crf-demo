package awsm.banking;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import awsm.crm.CustomerHashId;
import org.springframework.stereotype.Component;

class ApplyForBankAccount implements Command<Voidy> {

  private final String customerHashId;
  private final String currency;

  ApplyForBankAccount(String customerHashId, String currency) {
    this.customerHashId = customerHashId;
    this.currency = currency;
  }

  @Component
  static class H implements Handler<ApplyForBankAccount, Voidy> {

    @Override
    public Voidy handle(ApplyForBankAccount cmd) {
      var customerId = new CustomerHashId(cmd.customerHashId).idInstance();
      System.out.println(customerId + " has applied for account with currency " + cmd.currency);
      return new Voidy();
    }
  }
}
