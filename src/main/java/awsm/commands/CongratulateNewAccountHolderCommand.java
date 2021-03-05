package awsm.commands;

import static java.lang.String.format;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import awsm.domain.banking.BankAccountRepository;
import java.time.LocalDate;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.springframework.stereotype.Component;

public class CongratulateNewAccountHolderCommand implements Command<Voidy> {

  final String iban;
  final LocalDate date;

  public CongratulateNewAccountHolderCommand(String iban, LocalDate date) {
    this.date = date;
    this.iban = iban;
  }

  @Component
  static class Handler implements Command.Handler<CongratulateNewAccountHolderCommand, Voidy> {

    private final BankAccountRepository accountRepo;

    private final Mailer mailer;

    private Handler(BankAccountRepository accountRepo, Mailer mailer) {
      this.accountRepo = accountRepo;
      this.mailer = mailer;
    }

    @Override
    public Voidy handle(CongratulateNewAccountHolderCommand cmd) {
      var account = accountRepo.getOne(cmd.iban);
      var email =
          EmailBuilder.startingBlank()
              .to(account.holder().email())
              .withSubject("Congratulations!")
              .withPlainText(
                  format(
                      "Congratulations, %s. Thanks for using our services in %s",
                      account.holder().name(), cmd.date.getYear()))
              .buildEmail();
      mailer.sendMail(email);
      return new Voidy();
    }
  }
}
