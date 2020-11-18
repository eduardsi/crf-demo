package awsm.application;


import awsm.domain.crm.Uniqueness;
import awsm.infrastructure.validation.Validator;
import awsm_dagger.Command;
import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import jooq.tables.records.CustomerRecord;
import org.jasypt.util.text.TextEncryptor;

import static com.google.common.base.Strings.isNullOrEmpty;

@AutoFactory
public class RegisterNow implements Command<RegisterNow.RegistrationOk> {

    private final Uniqueness uniqueness;
    private final TextEncryptor encryptor;
    private final String firstName;
    private final String lastName;
    private final String personalId;
    private final String email;

    public RegisterNow(@Provided Uniqueness uniqueness, @Provided TextEncryptor encryptor, String firstName, String lastName, String personalId, String email) {
        this.uniqueness = uniqueness;
        this.encryptor = encryptor;
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalId = personalId;
        this.email = email;
    }

    @Override
    public RegistrationOk execute() {
        validate();

        var customer = new CustomerRecord(personalId, firstName, lastName, email);
        customer.insert();

        return new RegistrationOk(encryptor.encrypt(personalId));
    }

    private void validate() {
        new Validator<>()
                .with(() -> firstName, v -> !isNullOrEmpty(v), "firstName is missing")
                .with(() -> lastName, v -> !isNullOrEmpty(v), "lastName is missing")
                .with(() -> personalId, v -> !isNullOrEmpty(v), "personalId is missing")
                .with(() -> email, v -> !isNullOrEmpty(v), "email is missing", nested ->
                        nested.with(() -> email, v -> uniqueness.guaranteed(v), "email is taken")
                )
                .check(this);
    }

    public static class RegistrationOk {

        public final String personalId;

        RegistrationOk(String personalId) {
            this.personalId = personalId;
        }
    }
}
