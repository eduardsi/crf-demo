package lightweight4j.lib.commands;

import com.google.common.reflect.TypeToken;

public interface Reaction<C extends Command<R>, R> {

    R react(C $);

    default boolean isApplicableFor(C $) {
        var typeToken = new TypeToken<C>(getClass()) {
        };

        return typeToken.isSupertypeOf($.getClass());
    }



}