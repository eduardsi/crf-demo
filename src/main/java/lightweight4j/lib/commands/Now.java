package lightweight4j.lib.commands;

public interface Now {

    <R, C extends Command<R>> R execute(C command);

}
