package lightweight4j.app.membership;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lightweight4j.lib.commands.Command;
import lightweight4j.lib.commands.Now;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BecomeAMember implements Command<String> {

    private String email;

    @RestController
    static class HttpEndpoint {

        private final Now now;

        public HttpEndpoint(Now now) {
            this.now = now;
        }

        @RequestMapping("/members")
        public String post(@RequestBody BecomeAMember command) {
            return now.execute(command);
        }

    }


}


