package lightweight4j.lib.commands;

@FunctionalInterface
public interface Pipe {

    <R, C extends Command<R>> R transport(C command);

}
