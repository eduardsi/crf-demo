package awsm.domain.banking;

import awsm.domain.core.Data;
import awsm.domain.core.DomainEvent;

public class BankAccountOpened extends Data implements DomainEvent {

    private final String iban;

    BankAccountOpened(String iban) {
        this.iban = iban;
    }

    public String iban() {
        return iban;
    }

}
