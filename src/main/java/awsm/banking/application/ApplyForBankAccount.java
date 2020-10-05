package awsm.banking.application;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import awsm.banking.domain.AccountHolder;
import awsm.banking.domain.BankAccount;
import awsm.banking.domain.BankAccountRepository;
import awsm.banking.domain.WithdrawalLimits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

record ApplyForBankAccount(String firstName, String lastName, String personalId) implements Command<ApplyForBankAccount.Response> {

    record Response(String iban) { }

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
            var holder = new AccountHolder(cmd.firstName, cmd.lastName, cmd.personalId);
            var withdrawalLimits = WithdrawalLimits.defaults(env);
            var account = new BankAccount(holder, withdrawalLimits);
            account.open();
            account.deposit(openingBonus());

            bankAccountRepository.save(account);
            return new Response(account.iban());
        }

        private BigDecimal openingBonus() {
            return new BigDecimal("5.00");
        }
    }

}
