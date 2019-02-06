package lightweight4j.lib.commands;

@FunctionalInterface
public interface PipelineBehavior {

    <R, C extends Command<R>> R mixIn(C command);

}
