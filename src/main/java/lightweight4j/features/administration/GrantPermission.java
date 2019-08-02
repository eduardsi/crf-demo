package lightweight4j.features.administration;

import an.awesome.pipelinr.Voidy;
import lightweight4j.lib.pipeline.ExecutableCommand;

public class GrantPermission extends ExecutableCommand<Voidy> {

    public final Long adminId;
    public final String operation;

    public GrantPermission(Long adminId, String operation) {
        this.adminId = adminId;
        this.operation = operation;
    }

}
