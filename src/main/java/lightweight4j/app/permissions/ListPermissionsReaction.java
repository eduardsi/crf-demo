package lightweight4j.app.permissions;

import lightweight4j.lib.commands.Reaction;
import lightweight4j.app.membership.Members;
import org.springframework.stereotype.Component;

import static java.util.stream.Collectors.toCollection;

@Component
class ListPermissionsReaction implements Reaction<ListPermissions, ListPermissions.PermissionNames> {

    private final Members members;

    public ListPermissionsReaction(Members members) {
        this.members = members;
    }

    @Override
    public ListPermissions.PermissionNames react(ListPermissions $) {
        var member = members
                .findById($.memberId())
                .orElseThrow(() -> new IllegalArgumentException("Member cannot be found by id"));

        return member
                .permissions()
                .stream()
                .map(Permission::name)
                .map(Permission.UniqueName::toString)
                .collect(toCollection(ListPermissions.PermissionNames::new));
    }
}
