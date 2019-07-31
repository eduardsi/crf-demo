package lightweight4j.features.registration;

import lightweight4j.lib.pipeline.ExecutableCommand;

public class GetMemberInfo implements ExecutableCommand<GetMemberInfo.MemberInfo> {

    public final Long memberId;

    public GetMemberInfo(Long memberId) {
        this.memberId = memberId;
    }

    public static class MemberInfo {

        public final String firstName;
        public final String lastName;

        public MemberInfo(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }
}
