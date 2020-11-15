package awsm.banking.domain;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import awsm.infrastructure.scheduling.ScheduledCommandId;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.springsupport.SimpleJavaMailSpringSupport;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import static awsm.banking.domain.CongratulateNewAccountHolder.ID;
import static java.lang.String.format;

@ScheduledCommandId(ID)
class CongratulateNewAccountHolder implements Command<Voidy> {

  static final String ID = "Congratulations";

  final String iban;

  CongratulateNewAccountHolder(String iban) {
    this.iban = iban;
  }

  @Component
  @Import(SimpleJavaMailSpringSupport.class)
  static class Handler implements Command.Handler<CongratulateNewAccountHolder, Voidy> {

    private final BankAccountRepository bankAccountRepository;

    private final Mailer mailer;

    private Handler(BankAccountRepository bankAccountRepository, Mailer mailer) {
      this.bankAccountRepository = bankAccountRepository;
      this.mailer = mailer;
    }

    @Override
    public Voidy handle(CongratulateNewAccountHolder cmd) {
      var account = bankAccountRepository.getOne(cmd.iban);
      var email = EmailBuilder
              .startingBlank()
              .to(account.holder().email())
              .withSubject("Congratulations!")
              .withPlainText(format("Congratulations, %s. Thanks for using our services", account.holder().name()))
              .buildEmail();
      mailer.sendMail(email);
      return new Voidy();
    }
  }

}
