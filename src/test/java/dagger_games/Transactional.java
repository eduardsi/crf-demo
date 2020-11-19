package dagger_games;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import org.jooq.DSLContext;

@AutoFactory
class Transactional<R> implements Command<R> {

    private final DSLContext dsl;
    private final Command<R> wrapped;

    Transactional(@Provided DSLContext dsl, Command<R> wrapped) {
        this.dsl = dsl;
        this.wrapped = wrapped;
    }

    @Override
    public R execute() {
        return dsl.transactionResult(() -> wrapped.execute());
    }
}
