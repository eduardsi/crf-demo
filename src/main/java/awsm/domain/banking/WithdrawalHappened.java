package awsm.domain.banking;

import awsm.domain.core.Data;
import awsm.domain.core.DomainEvent;

import java.util.UUID;

public class WithdrawalHappened extends Data implements DomainEvent {

    private final String iban;
    private final UUID txUid;

    public WithdrawalHappened(String iban, UUID txUid) {
        this.iban = iban;
        this.txUid = txUid;
    }

    String iban() {
        return iban;
    }

    UUID txUid() {
        return txUid;
    }

}
