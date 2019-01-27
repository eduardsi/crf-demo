package net.sizovs.crf.services.membership;

import net.sizovs.crf.backbone.Now;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class BecomeAMemberEndpoint {

    private final Now now;

    @Autowired
    public BecomeAMemberEndpoint(Now now) {
        this.now = now;
    }

    @RequestMapping("/members")
    public String post(BecomeAMember command) {
        return command.execute(now);
    }

}