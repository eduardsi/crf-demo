package net.sizovs.crf.services.permissions;

import com.google.common.collect.ForwardingCollection;
import net.sizovs.crf.backbone.Command;

import java.util.ArrayList;
import java.util.Collection;

public class ListPermissions implements Command<ListPermissions.PermissionNames> {

    private final String memberId;

    public ListPermissions(String memberId) {
        this.memberId = memberId;
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





