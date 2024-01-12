package shordinger.wrapper.net.minecraft.command;

public class CommandException extends net.minecraft.command.CommandException {

    private final Object[] errorObjects;

    public CommandException(String message, Object... objects) {
        super(message);
        this.errorObjects = objects;
    }

    public Object[] getErrorObjects() {
        return this.errorObjects;
    }

    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
