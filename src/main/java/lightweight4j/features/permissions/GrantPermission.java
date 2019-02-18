package lightweight4j.features.permissions;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import lightweight4j.features.membership.impl.Members;
import lightweight4j.features.permissions.impl.Permissions;
import org.springframework.stereotype.Component;

public class GrantPermission implements Command<Voidy> {

    private final String memberId;
    private final String permissionId;

    public GrantPermission(String memberId, String permissionId) {
        this.memberId = memberId;
        this.permissionId = permissionId;
    }

    @Component
    static class Handler implements Command.Handler<GrantPermission, Voidy> {

        private final Members members;
        private final Permissions permissions;

        public Handler(Members members, Permissions permissions) {
            this.members = members;
            this.permissions = permissions;
        }

        @Override
        public Voidy handle(GrantPermission $) {
            var member = members.findById($.memberId).orElseThrow(() -> new IllegalArgumentException("Member cannot be found by id"));

            var permission = permissions.findById($.permissionId)
                    .orElseThrow(() -> new IllegalArgumentException("Cannot find permission (" + $.permissionId + ")"));

            member.grant(permission);

            return new Voidy();
        }
    }
}
