package awsm.commands;

import an.awesome.pipelinr.Command;
import awsm.domain.banking.AccountHolder;
import awsm.domain.banking.BankAccount;
import awsm.domain.banking.BankAccountRepository;
import awsm.domain.banking.WithdrawalLimits;
import awsm.domain.core.Amount;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

public class ApplyForBankAccountCommand implements Command<ApplyForBankAccountCommand.Response> {
    public final String firstName;
    public final String lastName;
    public final String personalId;
    public final String email;
    ApplyForBankAccountCommand(String firstName, String lastName, String personalId, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalId = personalId;
        this.email = email;
    }

    public static class Response {
        public final String iban;
        public Response(String iban) {
            this.iban = iban;
        }
    }

    @Component
    static class Handler implements Command.Handler<ApplyForBankAccountCommand, Response> {
        private final BankAccountRepository repo;
        private final Environment env;
        Handler(BankAccountRepository repo, Environment env) {
            this.repo = repo;
            this.env = env;
        }

        @Override
        public Response handle(ApplyForBankAccountCommand cmd) {
            var withdrawalLimits = WithdrawalLimits.defaults(env);
            var accountHolder = new AccountHolder(cmd.firstName, cmd.lastName, cmd.personalId, cmd.email);
            var account = new BankAccount(accountHolder, withdrawalLimits);
            account.open();
            account.deposit(openingBonus());
            repo.save(account);
            return new Response(account.iban());
        }

        private Amount openingBonus() {
            return Amount.of("5.00");
        }
    }
}
