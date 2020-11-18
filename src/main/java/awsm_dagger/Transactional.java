package awsm_dagger;

import org.jooq.DSLContext;

class Transactional<R> implements Command<R> {

    private DSLContext dsl;
    private final Command<R> wrapped;

    Transactional(DSLContext dsl, Command<R> wrapped) {
        this.dsl = dsl;
        this.wrapped = wrapped;
    }

    @Override
    public R execute() {
        return dsl.transactionResult(() -> wrapped.execute());
    }
}
