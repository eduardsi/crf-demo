package awsm.application;


import awsm.domain.crm.Uniqueness;
import awsm.infrastructure.validation.Validator;
import jooq.tables.records.CustomerRecord;
import org.jasypt.util.text.TextEncryptor;

import static com.google.common.base.Strings.isNullOrEmpty;

public class RegistrationFacade {

    private final Uniqueness uniqueness;
    private final TextEncryptor encryptor;

    public RegistrationFacade(Uniqueness uniqueness, TextEncryptor encryptor) {
        this.uniqueness = uniqueness;
        this.encryptor = encryptor;
    }

    public RegistrationOk register(String firstName, String lastName, String personalId, String email) {
        new Validator<>()
                .with(() -> firstName, v -> !isNullOrEmpty(v), "firstName is missing")
                .with(() -> lastName, v -> !isNullOrEmpty(v), "lastName is missing")
                .with(() -> personalId, v -> !isNullOrEmpty(v), "personalId is missing")
                .with(() -> email, v -> !isNullOrEmpty(v), "email is missing", nested ->
                        nested.with(() -> email, v -> uniqueness.guaranteed(v), "email is taken")
                )
                .check(this);

        var customer = new CustomerRecord(personalId, firstName, lastName, email);
        customer.insert();

        return new RegistrationOk(encryptor.encrypt(personalId));
    }

    static class RegistrationOk {

        public final String personalId;

        RegistrationOk(String personalId) {
            this.personalId = personalId;
        }
    }
}
