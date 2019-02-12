package lightweight4j.app.permissions;

import an.awesome.pipelinr.Command;
import com.google.common.collect.ForwardingCollection;
import lightweight4j.app.membership.Members;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toCollection;

public class ListPermissions implements Command<CompletableFuture<ListPermissions.PermissionNames>> {

    private final String memberId;

    public ListPermissions(String memberId) {
        this.memberId = memberId;
    }

    public static class PermissionNames extends ForwardingCollection<String> {

        private final Collection<String> names = new ArrayList<>();

        @Override
        protected Collection<String> delegate() {
            return names;
        }
    }

    @Component
    static class Handler implements Command.Handler<ListPermissions, CompletableFuture<PermissionNames>> {

        private final Members members;

        public Handler(Members members) {
            this.members = members;
        }

        @Override
        public CompletableFuture<PermissionNames> handle(ListPermissions $) {
            var member = members
                    .findById($.memberId)
                    .orElseThrow(() -> new IllegalArgumentException("Member cannot be found by id"));

            return CompletableFuture.completedFuture(
                    member
                            .permissions()
                            .stream()
                            .map(Permission::name)
                            .map(Permission.UniqueName::toString)
                            .collect(toCollection(PermissionNames::new)));
        }
    }
}





