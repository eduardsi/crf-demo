package lightweight4j.lib.commands;

public interface Command<R> {

    class Void {
        @Override
        public String toString() {
            return "Void";
        }
    }

}
