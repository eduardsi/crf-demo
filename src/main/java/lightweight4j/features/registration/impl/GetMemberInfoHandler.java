package lightweight4j.features.registration.impl;

import an.awesome.pipelinr.Command;
import lightweight4j.features.registration.GetMemberInfo;
import org.springframework.stereotype.Component;

@Component
class GetMemberInfoHandler implements Command.Handler<GetMemberInfo, GetMemberInfo.MemberInfo> {

    private final Members members;

    public GetMemberInfoHandler(Members members) {
        this.members = members;
    }

    @Override
    public GetMemberInfo.MemberInfo handle(GetMemberInfo command) {
        var member = members.findById(command.memberId).orElseThrow();
        return new GetMemberInfo.MemberInfo(member.name.firstOne, member.name.lastOne);
    }
}
