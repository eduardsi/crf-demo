package net.sizovs.crf;

import net.sizovs.crf.backbone.Future;
import net.sizovs.crf.backbone.Now;
import net.sizovs.crf.services.membership.BecomeAMember;
import net.sizovs.crf.services.permissions.CreatePermission;
import net.sizovs.crf.services.permissions.GrantPermission;
import net.sizovs.crf.services.permissions.ListPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.function.Consumer;

@SpringBootApplication
public class ApplicationBootstrap implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ApplicationBootstrap.class);

    private final Now now;

    private final Future future;

    public ApplicationBootstrap(Now now, Future future) {
        this.now = now;
        this.future = future;
    }

    @Override
    public void run(String... args) {

        var createPermission = new CreatePermission("Superpowers");
        var permissionId = createPermission.execute(now);

        var becomeAMember = new BecomeAMember("alan@devternity.com");
        var memberId = becomeAMember.execute(now);

        var grantPermission = new GrantPermission(memberId.toString(), permissionId.toString());
        grantPermission.execute(now);

        var listPermissions = new ListPermissions(memberId.toString());
        listPermissions.schedule(future).thenAccept(logPermissions());
    }

    private Consumer<ListPermissions.PermissionNames> logPermissions() {
        return permissionNames -> log.info("Alan has permissions {}", permissionNames);
    }

    public static void main(String[] args) {
        SpringApplication.run(ApplicationBootstrap.class, args);
    }
}
