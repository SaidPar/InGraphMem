package commands;

public final class CmdConsts {

  public static String EXIT = "exit";
  public static String SHUTDOWN_DATABASE = "shutdownDatabase";
  public static String START_DATABASE = "startDatabase";
  public static String QUERY = "query";

  // Start Database Switches
  public static String DB_NAME = "-databaseName";
  public static String DB_STORAGE = "-dbStorage";

  // DB_STORAGE OPTIONS
  public static String IN_MEMORY = "inMemory";
  public static String PORTABLE = "portable";

  // Query Switches
  public static String OPEN_CYPHER = "-openCypher";

  private CmdConsts() {
  }
}
