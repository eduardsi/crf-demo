package net.sizovs.crf.backbone;

public interface Now {

    <R, C extends Command<R>> R execute(C command);

}
