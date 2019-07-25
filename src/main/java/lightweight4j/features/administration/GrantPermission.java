package lightweight4j.features.administration;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import org.springframework.stereotype.Component;


public class GrantPermission implements Command<Voidy> {

    private final String adminId;
    private final String operation;

    public GrantPermission(String adminId, String operation) {
        this.adminId = adminId;
        this.operation = operation;
    }

    @Component
    static class Handler implements Command.Handler<GrantPermission, Voidy> {

        private final Administrators admins;

        public Handler(Administrators admins) {
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
