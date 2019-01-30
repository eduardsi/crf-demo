package lightweight4j.app.permissions;

import com.google.common.collect.ForwardingCollection;
import lombok.Value;
import lightweight4j.lib.commands.Command;

import java.util.ArrayList;
import java.util.Collection;

@Value
public class ListPermissions implements Command<ListPermissions.PermissionNames> {

    private final String memberId;

    public static class PermissionNames extends ForwardingCollection<String> {

        private final Collection<String> ids = new ArrayList<>();

        @Override
        protected Collection<String> delegate() {
            return ids;
        }
    }

}





