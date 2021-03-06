package awsm.api;

import awsm.domain.banking.AccountHolder;
import awsm.domain.banking.BankAccount;
import awsm.domain.banking.BankAccountRepository;
import awsm.domain.banking.WithdrawalLimits;
import awsm.domain.core.Amount;
import lombok.Data;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class BankAccountController {

  private final BankAccountRepository repo;
  private final Environment env;

  BankAccountController(BankAccountRepository repo, Environment env) {
    this.repo = repo;
    this.env = env;
  }

  @PostMapping("/bank-accounts")
  @Transactional
  ResponseDto applyForBankAccount(@RequestBody RequestDto request) {
    var withdrawalLimits = WithdrawalLimits.defaults(env);
    var accountHolder = new AccountHolder(request.firstName, request.lastName, request.email);
    var account = new BankAccount(accountHolder, withdrawalLimits);
    account.open();
    account.deposit(openingBonus());
    repo.save(account);
    return new ResponseDto(account.iban());
  }

  private Amount openingBonus() {
    return Amount.of("5.00");
  }

  @Data
  static class RequestDto {
    public final String firstName;
    public final String lastName;
    public final String personalId;
    public final String email;
  }

  @Data
  static class ResponseDto {
    public final String iban;
  }
}
