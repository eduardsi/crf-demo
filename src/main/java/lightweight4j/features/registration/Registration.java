package lightweight4j.features.registration;

import lightweight4j.lib.pipeline.ExecutableCommand;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;

public class Registration implements ExecutableCommand<Long> {

    @NotEmpty
    public final String email;

    @NotEmpty
    public final String firstName;

    @NotEmpty
    public final String lastName;

    public Registration(String email, String firstName, String lastName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Component
    @RestController
    static class ViaHttp {

        @PostMapping("/members")
        Long accept(@RequestBody Registration command) {
            return command.execute();
        }

    }
}
