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

public class ApplyForBankAccount implements Command<String> {

    private final String firstName;
    private final String lastName;
    private final String personalId;

    ApplyForBankAccount(String firstName, String lastName, String personalId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalId = personalId;
    }

    static class Response {
        private final String iban;

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
            String iban = pipeline.send(command);
            return new Response(iban);
        }
    }

    @Component
    static class Handler implements Command.Handler<ApplyForBankAccount, String> {

        private final BankAccountRepository bankAccountRepository;
        private final Environment env;

        Handler(BankAccountRepository bankAccountRepository, Environment env) {
            this.bankAccountRepository = bankAccountRepository;
            this.env = env;
        }

        @Override
        public String handle(ApplyForBankAccount cmd) {
            var holder = new AccountHolder(cmd.firstName, cmd.lastName, cmd.personalId);
            var withdrawalLimits = WithdrawalLimits.defaults(env);
            var account = new BankAccount(holder, withdrawalLimits);
            account.open();
            account.deposit(openingBonus());

            bankAccountRepository.save(account);
            return account.iban();
        }

        private BigDecimal openingBonus() {
            return new BigDecimal("5.00");
        }
    }

}
