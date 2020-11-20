package dagger_games;

import org.jooq.DSLContext;

import static jooq.tables.BankAccount.BANK_ACCOUNT;

public class BankAccountByIban {

    private final DSLContext dsl;
    private final String iban;

    BankAccountByIban(DSLContext dsl, String iban) {
        this.dsl = dsl;
        this.iban = iban;
    }

    BankAccount find() {
        var self = dsl
                .selectFrom(BANK_ACCOUNT)
                .where(BANK_ACCOUNT.IBAN.equal(iban))
                .fetchOne();

        // LEFT JOIN example
//        var where = dsl
//                .select()
//                .from(BANK_ACCOUNT)
//                .join(BANK_ACCOUNT_TX)
//                .on(BANK_ACCOUNT_TX.BANK_ACCOUNT_IBAN.eq(BANK_ACCOUNT.IBAN))
//                .where(BANK_ACCOUNT.IBAN.equal(iban))
//                .fetchGroups(BANK_ACCOUNT, BANK_ACCOUNT_TX)
//                .entrySet()
//                .stream()
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("Cannot fetch bank account by iban '" + iban + "'"));

        return new BankAccount(self);
    }

}
