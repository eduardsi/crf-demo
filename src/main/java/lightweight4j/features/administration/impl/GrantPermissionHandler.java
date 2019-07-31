package lightweight4j.features.administration.impl;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import lightweight4j.features.administration.GrantPermission;
import lightweight4j.features.registration.GetMemberInfo;
import org.springframework.stereotype.Component;

@Component
class GrantPermissionHandler implements Command.Handler<GrantPermission, Voidy> {
    
    private final Administrators admins;

    public GrantPermissionHandler(Administrators admins) {
        this.admins = admins;
    }

    @Override
    public Voidy handle(GrantPermission $) {
        var memberInfo = new GetMemberInfo($.adminId).execute();
        System.out.println(memberInfo);
        var admin = admins.findById($.adminId).orElseThrow(() -> new IllegalArgumentException("Admin cannot be found by id"));
        admin.grant(Permission.toDo($.operation));
        return new Voidy();
    }

}