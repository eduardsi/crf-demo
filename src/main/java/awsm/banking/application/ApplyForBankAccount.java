package awsm.banking.application;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import awsm.banking.domain.AccountHolder;
import awsm.banking.domain.BankAccount;
import awsm.banking.domain.BankAccountRepository;
import awsm.banking.domain.WithdrawalLimits;
import awsm.banking.domain.core.Amount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

class ApplyForBankAccount implements Command<ApplyForBankAccount.Response> {

    public final String firstName;
    public final String lastName;
    public final String personalId;
    public final String email;

    ApplyForBankAccount(String firstName, String lastName, String personalId, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalId = personalId;
        this.email = email;
    }

    static class Response {

        public final String iban;

        Response(String iban) {
            this.iban = iban;
        }
    }

    @RestController
    static class WebController {

        @Autowired
        Pipeline pipeline;

        @PostMapping("/bank-accounts")
        Response post(@RequestBody ApplyForBankAccount command) {
            return pipeline.send(command);
        }
    }

    @Component
    static class Handler implements Command.Handler<ApplyForBankAccount, ApplyForBankAccount.Response> {

        private final BankAccountRepository bankAccountRepository;
        private final Environment env;

        Handler(BankAccountRepository bankAccountRepository, Environment env) {
            this.bankAccountRepository = bankAccountRepository;
            this.env = env;
        }

        @Override
        public Response handle(ApplyForBankAccount cmd) {
            var holder = new AccountHolder(cmd.firstName, cmd.lastName, cmd.personalId, cmd.email);
            var withdrawalLimits = WithdrawalLimits.defaults(env);
            var account = new BankAccount(holder, withdrawalLimits);
            account.open();
            account.deposit(openingBonus());

            bankAccountRepository.save(account);
            return new Response(account.iban());
        }

        private Amount openingBonus() {
            return Amount.of("5.00");
        }
    }

}
