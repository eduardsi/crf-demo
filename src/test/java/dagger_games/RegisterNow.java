package dagger_games;

import awsm.infrastructure.validation.Validator;
import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import jooq.tables.records.CustomerRecord;
import org.jasypt.util.text.TextEncryptor;
import org.jooq.DSLContext;

import java.util.function.Predicate;

import static jooq.Tables.CUSTOMER;

@AutoFactory
public class RegisterNow implements Command<RegisterNow.RegistrationOk> {

    private final TextEncryptor encryptor;
    private final DSLContext dsl;
    private final WithdrawalLimits withdrawalLimits;

    private final String firstName;
    private final String lastName;
    private final String personalId;
    private final String email;

    public RegisterNow(@Provided DSLContext dsl, @Provided TextEncryptor encryptor, @Provided WithdrawalLimits withdrawalLimits, String firstName, String lastName, String personalId, String email) {
        this.dsl = dsl;
        this.encryptor = encryptor;
        this.withdrawalLimits = withdrawalLimits;
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalId = personalId;
        this.email = email;
    }

    @Override
    public RegistrationOk execute() {

        validate();

        var customer = new CustomerRecord(personalId, firstName, lastName, email);
        dsl.insertInto(CUSTOMER).set(customer).execute();

        var accountHolder = new BankAccountHolder("Eduards", "Sizovs", "2112412", "eduards@sizovs.net");
        var account = new BankAccount(accountHolder, withdrawalLimits);
        account.deposit(Amount.of("100.00"));
        account.deposit(Amount.of("100.00"));
        account.deposit(Amount.of("100.00"));
        account.withdraw(Amount.of("5.00"));
        account.save(dsl);

        var iban = account.iban();

        var accountByIban = new BankAccountByIban(dsl, iban).find();
        System.out.println(accountByIban.balance());

        return new RegistrationOk(encryptedPersonalId());
    }

    private void validate() {
        Predicate<String> isUnique = email -> !dsl.fetchExists(dsl.selectFrom(CUSTOMER).where(CUSTOMER.EMAIL.eq(email)));
        new Validator<>()
                .with(() -> firstName, v -> !v.isBlank(), "firstName is missing")
                .with(() -> lastName, v -> !v.isBlank(), "lastName is missing")
                .with(() -> personalId, v -> !v.isBlank(), "personalId is missing")
                .with(() -> email, v -> !v.isBlank(), "email is missing", nested ->
                        nested.with(() -> email, isUnique, "email is taken")
                ).check(this);
    }

    private String encryptedPersonalId() {
        return encryptor.encrypt(personalId);
    }

    public static class RegistrationOk {

        public final String personalId;

        RegistrationOk(String personalId) {
            this.personalId = personalId;
        }
    }
}
