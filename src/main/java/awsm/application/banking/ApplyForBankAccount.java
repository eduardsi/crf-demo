package awsm.application.banking;

import awsm.infrastructure.middleware.Command;

public class ApplyForBankAccount implements Command<ApplyForBankAccount.Response> {

  public final String customerHashId;
  public final String currency;

  ApplyForBankAccount(String customerHashId, String currency) {
    this.customerHashId = customerHashId;
    this.currency = currency;
  }

  public static class Response {

  }

}
