package net.sizovs.crf.services.permissions;

import net.sizovs.crf.backbone.Reaction;
import net.sizovs.crf.services.membership.Members;
import net.sizovs.crf.services.permissions.ListPermissions.PermissionNames;
import org.springframework.stereotype.Component;

import static java.util.stream.Collectors.toCollection;

@Component
class ListPermissionsReaction implements Reaction<ListPermissions, PermissionNames> {

    private final Members members;

    public ListPermissionsReaction(Members members) {
        this.members = members;
    }

    @Override
    public PermissionNames react(ListPermissions $) {
        var member = members.findOne($.memberId());
        return member
                .permissions()
                .stream()
                .map(Permission::name)
                .map(Permission.UniqueName::toString)
                .collect(toCollection(PermissionNames::new));
    }
}
