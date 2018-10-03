package net.sizovs.crf.services.permissions;

import com.google.common.collect.ForwardingCollection;
import net.sizovs.crf.backbone.Command;
import net.sizovs.crf.services.membership.MemberId;

import java.util.ArrayList;
import java.util.Collection;

public class ListPermissions implements Command<ListPermissions.PermissionNames> {

    private final String memberId;

    public ListPermissions(MemberId memberId) {
        this.memberId = memberId.toString();
    }

    public String memberId() {
        return memberId;
    }

    public static class PermissionNames extends ForwardingCollection<String> implements Command.R {

        private final Collection<String> ids = new ArrayList<>();

        @Override
        protected Collection<String> delegate() {
            return ids;
        }
    }

}





