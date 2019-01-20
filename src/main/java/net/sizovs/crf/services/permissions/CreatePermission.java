package net.sizovs.crf.services.permissions;

import lombok.Value;
import net.sizovs.crf.backbone.Command;

@Value
public class CreatePermission implements Command<String> {

    private final String name;

}
