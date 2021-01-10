package commands;

public class ShutdownCommand implements Commandable {

    private final String[] commands;

    public ShutdownCommand(String[] commands) {
        this.commands = commands;
    }

    @Override
    public void parseOptions() {

    }

    @Override
    public boolean validateOptions() {
        return false;
    }

    @Override
    public CmdOp getCommandType() {
        return CmdOp.SHUTDOWN_DB;
    }

    @Override
    public void help() {

    }

    @Override
    public void execute() {

    }
}
