package lightweight4j.application.commands;

import an.awesome.pipelinr.Voidy;
import lightweight4j.domain.administration.Administrators;
import lightweight4j.domain.administration.Permission;
import lightweight4j.infra.pipeline.ExecutableCommand;
import org.springframework.stereotype.Component;

public class GrantPermission extends ExecutableCommand<Voidy> {

    private final Long adminId;
    private final String operation;

    public GrantPermission(Long adminId, String operation) {
        this.adminId = adminId;
        this.operation = operation;
    }

    @Component
    static class GrantPermissionHandler implements Handler<GrantPermission, Voidy> {

        private final Administrators admins;

        public GrantPermissionHandler(Administrators admins) {
            this.admins = admins;
        }

        @Override
        public Voidy handle(GrantPermission $) {
            var admin = admins.findById($.adminId).orElseThrow(() -> new IllegalArgumentException("Admin cannot be found by id"));
            admin.grant(Permission.toDo($.operation));
            return new Voidy();
        }

    }
}
