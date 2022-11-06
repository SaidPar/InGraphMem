package commands;

import common.Logger;
import database.Database;
import database.Instance;
import query.engine.ResultSet;

public class QueryCommand implements Commandable {

  private static Logger logger = Logger.getLogger();
  private final String[] commands;

  private String dbName;
  private String openCypherStr;


  QueryCommand(String[] commands) {
    this.commands = commands;
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Commandable Interface Methods
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public void parseOptions() {
    if (commands.length < 3) {
      logger.error("invalid number of arguments.");
      help();
      return;
    }

    for (int i = 1; i < commands.length; i++) {
      String option = commands[i];

      if (option.equalsIgnoreCase(CmdConsts.OPEN_CYPHER)) {
        this.openCypherStr = commands[i + 1];
      } else if (option.equalsIgnoreCase(CmdConsts.DB_NAME)) {
        this.dbName = commands[i + 1];
      }
    }
  }

  @Override
  public boolean validateOptions() {
    return
      (null != openCypherStr && !openCypherStr.isBlank() && !openCypherStr.isEmpty()) &&
      (null != dbName && !dbName.isBlank() && !dbName.isEmpty());
  }

  @Override
  public CmdOp getCommandType() {
    return CmdOp.QUERY;
  }

  @Override
  public void help() {
    logger.header("QUERY");
    logger.log(CmdConsts.DB_NAME);
    logger.log(CmdConsts.OPEN_CYPHER);

    // ToDo: Descriptions
  }

  @Override
  public void execute() {
    try {
      Database db = Instance.getInstance().getDatabase(dbName);
      if (null == db) {
        logger.error("Database, '" + dbName + "' was not found.");
        return;
      }

      ResultSet resultSet = db.query(openCypherStr);
      logger.log(resultSet.toString());

    } catch (Exception e) {
      logger.error("An error occurred while querying the database.");
    }
  }
}
