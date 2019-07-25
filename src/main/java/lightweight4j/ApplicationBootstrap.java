package lightweight4j;

import an.awesome.pipelinr.Pipeline;
import lightweight4j.features.administration.GrantPermission;
import lightweight4j.features.membership.BecomeAMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
class ApplicationBootstrap implements CommandLineRunner {

    @Autowired
    Pipeline pipeline;

    @Override
    public void run(String... args) {
        Long memberId = pipeline.send(new BecomeAMember("eduards@sizovs.net", "Eduards", "Sizovs"));
        pipeline.send(new GrantPermission(memberId, "BACKOFFICE_ADMINISTRATION"));

    }

    public static void main(String[] args) {
        SpringApplication.run(ApplicationBootstrap.class, args);
    }
}
