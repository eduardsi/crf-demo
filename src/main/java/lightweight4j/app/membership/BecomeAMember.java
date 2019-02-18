package lightweight4j.app.membership;


import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import com.fasterxml.jackson.annotation.JsonProperty;
import lightweight4j.lib.jackson.Issue1498;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;

public class BecomeAMember implements Command<String> {

    @NotEmpty
    private final String email;

    public BecomeAMember(@Issue1498 @JsonProperty("email") String email) {
        this.email = email;
    }

    @RestController
    static class Http {

        private final Pipeline pipeline;

        Http(Pipeline pipeline) {
            this.pipeline = pipeline;
        }

        @RequestMapping("/members")
        public String post(@RequestBody BecomeAMember command) {
            return pipeline.send(command);
        }

    }


    @Component
    static class Handler implements Command.Handler<BecomeAMember, String> {

        private final Members members;
        private final EmailBlacklist blacklist;

        @Autowired
        public Handler(Members members, EmailBlacklist blacklist) {
            this.members = members;
            this.blacklist = blacklist;
        }

        @Override
        public String handle(BecomeAMember $) {
            var member = new Member(new Email($.email, blacklist));
            members.save(member);
            return member.id();
        }

    }
}


