package net.sizovs.crf.services.permissions;

import com.google.common.collect.ForwardingCollection;
import lombok.Value;
import net.sizovs.crf.backbone.Command;

import java.util.ArrayList;
import java.util.Collection;

@Value
public class ListPermissions implements Command<ListPermissions.PermissionNames> {

    private final String memberId;

    public static class PermissionNames extends ForwardingCollection<String> implements Command.R {

        private final Collection<String> ids = new ArrayList<>();

        @Override
        protected Collection<String> delegate() {
            return ids;
        }
    }

}





