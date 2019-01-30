package lightweight4j.lib.commands;

import com.google.common.collect.ForwardingCollection;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
class Router {

    private final AllReactions reactions;

    public Router(AllReactions reactions) {
        this.reactions = reactions;
    }

    @SuppressWarnings("unchecked")
    public <C extends Command<R>, R> Reaction<C, R> route(C command) {
        return reactions
                    .stream()
                    .filter(reaction -> reaction.isApplicableFor(command))
                    .findFirst()
                    .orElseThrow(() -> new NoReactionFound(command));
    }

    @Component
    static class AllReactions extends ForwardingCollection<Reaction> {

        private final ListableBeanFactory beanFactory;

        public AllReactions(ListableBeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        @Override
        protected Collection<Reaction> delegate() {
            return beanFactory.getBeansOfType(Reaction.class).values();
        }
    }
}