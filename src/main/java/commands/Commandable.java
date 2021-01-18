package commands;

/***
 * Interface responsible for decoding commands
 */
public interface Commandable {
  /**
   * Parses command options
   *
   * @param options
   */
  void parseOptions();

  /**
   * Called after parseOptions. Validates required options are populated and of valid values.
   */
  boolean validateOptions();

  /**
   * Returns the command type
   *
   * @return
   */
  CmdOp getCommandType();

  /**
   * Writes command help to console
   */
  void help();

  /**
   * Executes the command
   */
  void execute();
}
