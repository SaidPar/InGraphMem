package commands;

import common.Logger;

public class HelpCommand implements Commandable {

  private static Logger logger = Logger.getLogger();

  @Override
  public void parseOptions() {
  }

  @Override
  public boolean validateOptions() {
    return false;
  }

  @Override
  public CmdOp getCommandType() {
    return CmdOp.HELP;
  }

  @Override
  public void help() {
    logger.header("HELP");
  }

  @Override
  public void execute() {
    help();
  }
}
