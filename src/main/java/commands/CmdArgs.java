package commands;

import common.Logger;

public class CmdArgs {

  private static Logger logger = Logger.getLogger();
  private final Commandable cmd;

  public CmdArgs(String input) {
    String[] cmds = input.split(" ");
    cmd = parseOperation(cmds);
  }

  public CmdOp getOperation() {
    return cmd.getCommandType();
  }

  public Commandable getCommand() {
    return cmd;
  }

  private Commandable parseOperation(String[] commands) {

    try {
      if (commands.length == 0)
        return new HelpCommand();

      if (commands.length == 1) {
        if (commands[0].equalsIgnoreCase(CmdConsts.EXIT))
          return new ExitCommand();
        else
          return new HelpCommand();
      }

      if (commands[0].equalsIgnoreCase(CmdConsts.START_DATABASE))
        return new StartDBCommand(commands);

      if (commands[0].equalsIgnoreCase(CmdConsts.SHUTDOWN_DATABASE))
        return new ShutdownCommand(commands);

      if (commands[0].equalsIgnoreCase(CmdConsts.QUERY))
        return new QueryCommand(commands);

      return new HelpCommand();

    } catch (Exception e) {
      logger.error("An error occurred parsing operation: " + e.getMessage());
      return new HelpCommand();
    }
  }

  public void help() {
    cmd.help();
  }
}
