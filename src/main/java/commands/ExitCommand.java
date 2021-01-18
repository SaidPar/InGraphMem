package commands;

public class ExitCommand implements Commandable {
  @Override
  public void parseOptions() {

  }

  @Override
  public boolean validateOptions() {
    return true;
  }

  @Override
  public CmdOp getCommandType() {
    return CmdOp.EXIT;
  }

  @Override
  public void help() {

  }

  @Override
  public void execute() {
    // ToDo: Shutdown databases
  }
}
