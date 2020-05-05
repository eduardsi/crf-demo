package awsm.application;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import awsm.domain.banking.BankAccount;
import awsm.domain.banking.WithdrawalLimits;
import awsm.domain.banking.customer.Customer;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import static awsm.infrastructure.ids.Ids.decoded;

public class ApproveApplication implements Command<Voidy> {

  private final String customerId;

  public ApproveApplication(String customerId) {
    this.customerId = customerId;
  }

  @Component
  public static class Handler implements Command.Handler<ApproveApplication, Voidy> {

    private final BankAccount.Repository accountRepo;
    private final Customer.Repository customerRepo;
    private final Environment env;

    public Handler(
            BankAccount.Repository accountRepo,
            Customer.Repository customerRepo,
            Environment env) {
      this.accountRepo = accountRepo;
      this.customerRepo = customerRepo;
      this.env = env;
    }

    @Override
    public Voidy handle(ApproveApplication command) {
      var customerId = decoded(command.customerId);

      var customer = customerRepo.findBy(customerId);
      customer.confirm(customerRepo);

      var bankAccount = newBankAccount();
      bankAccount.open(accountRepo);

      return new Voidy();
    }

    private BankAccount newBankAccount() {
      var limits = WithdrawalLimits.DEFAULTS(env);
      return new BankAccount(limits);
    }
  }

}
