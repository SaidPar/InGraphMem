package query.engine.simple;

import common.Logger;
import database.Database;
import query.engine.IQueryEngine;
import query.engine.ResultSet;
import query.parser.OpenCypherParser;

public final class SimpleQueryEngine implements IQueryEngine {

  private static Logger logger = Logger.getLogger();
  private final Database db;

  public SimpleQueryEngine(Database db) {
    this.db = db;
  }

  public ResultSet query(String openCypher) {

    try {
      // parse openCypher
      var parseTree = new OpenCypherParser().parse(openCypher);

      // create execution plan


      // execute execution plan
      // populate ResultSet

      return new ResultSet();
    } catch (Exception e) {
      logger.error(e.getMessage());
      return new ResultSet();
    }
  }
}
