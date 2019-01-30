package lightweight4j.app.permissions;

import lightweight4j.lib.commands.Command;
import lightweight4j.lib.commands.Reaction;
import lightweight4j.app.membership.Members;
import org.springframework.stereotype.Component;

@Component
class GrantPermissionReaction implements Reaction<GrantPermission, Command.Void> {

    private final Members members;
    private final Permissions permissions;

    public GrantPermissionReaction(Members members, Permissions permissions) {
        this.members = members;
        this.permissions = permissions;
    }

    @Override
    public Command.Void react(GrantPermission $) {
        var member = members.findById($.memberId()).orElseThrow(() -> new IllegalArgumentException("Member cannot be found by id"));

        var permission = permissions.findById($.permissionId())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find permission (" + $.permissionId() + ")"));

        member.grant(permission);

        return new Command.Void();
    }
}
