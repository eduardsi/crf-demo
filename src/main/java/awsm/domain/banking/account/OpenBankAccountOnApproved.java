package awsm.domain.banking.account;

import awsm.domain.banking.BankService;
import awsm.infrastructure.middleware.DomainEventListener;
import org.jooq.DSLContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
class OpenBankAccountOnApproved implements DomainEventListener<BankService.BankAccountApproved> {

  private final Environment env;
  private final DSLContext db;

  OpenBankAccountOnApproved(Environment env, DSLContext db) {
    this.env = env;
    this.db = db;
  }

  @Override
  public void invoke(BankService.BankAccountApproved event) {
    // TODO: I do claim before open because persistence order. Where is our Hibernate? :)
    var bankAccount = new BankAccount(limits());
    bankAccount.claim(event.customer());
    bankAccount.open(db);
  }

  private WithdrawalLimits limits() {
    return WithdrawalLimits.DEFAULTS(env);
  }

}
