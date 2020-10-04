package awsm.banking;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import awsm.infrastructure.scheduling.ScheduledCommandId;
import org.springframework.stereotype.Component;

import static awsm.banking.CongratulateNewAccountHolder.ID;

@ScheduledCommandId(ID)
class CongratulateNewAccountHolder implements Command<Voidy> {

  static final String ID = "Congratulations";

  final String iban;

  CongratulateNewAccountHolder(String iban) {
    this.iban = iban;
  }

  @Component
  static class Handler implements Command.Handler<CongratulateNewAccountHolder, Voidy> {

    private final BankAccountRepository bankAccountRepository;

    private Handler(BankAccountRepository bankAccountRepository) {
      this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public Voidy handle(CongratulateNewAccountHolder cmd) {
      var account = bankAccountRepository.getOne(cmd.iban);
      System.out.printf("Congratulations,  %s. Thanks for using our services", account.holder().name());
      return new Voidy();
    }
  }

}
