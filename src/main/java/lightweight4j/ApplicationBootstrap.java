package lightweight4j;

import an.awesome.pipelinr.Pipeline;
import lightweight4j.app.membership.BecomeAMember;
import lightweight4j.app.permissions.CreatePermission;
import lightweight4j.app.permissions.GrantPermission;
import lightweight4j.app.permissions.ListPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.function.Consumer;

@SpringBootApplication
public class ApplicationBootstrap implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ApplicationBootstrap.class);

    private final Pipeline pipeline;

    public ApplicationBootstrap(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public void run(String... args) {

        var createPermission = new CreatePermission("Superpowers");
        var permissionId = pipeline.send(createPermission);

        var becomeAMember = new BecomeAMember("alan@devternity.com");
        var memberId = pipeline.send(becomeAMember);

        var grantPermission = new GrantPermission(memberId, permissionId);
        pipeline.send(grantPermission);

        var listPermissions = new ListPermissions(memberId);
        pipeline.send(listPermissions).thenAccept(logPermissions());

    }

    private Consumer<ListPermissions.PermissionNames> logPermissions() {
        return permissionNames -> log.info("Alan has permissions {}", permissionNames);
    }

    public static void main(String[] args) {
        SpringApplication.run(ApplicationBootstrap.class, args);
    }
}
