import commands.CmdArgs;
import commands.CmdOp;
import commands.Commandable;
import common.Logger;

import java.util.Scanner;

public class Entry {

  private static final Logger logger = Logger.getLogger();

  public static void main(String[] args) {

    printIntro();

    Scanner input = new Scanner(System.in);
    try {
      boolean canExit = false;
      while (!canExit) {
        logger.inputCarrot();
        canExit = processCommand(input.nextLine());
      }
    } catch (Exception e) {
      logger.error(e.getMessage());
    } finally {
      input.close();
    }
  }

  private static void printIntro() {
    logger.header("InGraphMemDB");
  }

  private static boolean processCommand(String input) {

    CmdArgs commandArgs = new CmdArgs(input);
    Commandable command = commandArgs.getCommand();
    command.parseOptions();
    if (!command.validateOptions()) {
      command.help();
    } else {
      command.execute();

      if (command.getCommandType() == CmdOp.EXIT)
        return true;
    }

    return false;
  }
}
