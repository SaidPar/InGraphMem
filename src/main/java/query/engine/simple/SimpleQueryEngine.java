package query.engine.simple;

import database.Database;
import query.engine.IQueryEngine;
import query.engine.ResultSet;

public final class SimpleQueryEngine implements IQueryEngine {

  private final Database db;

  public SimpleQueryEngine(Database db) {
    this.db = db;
  }

  public ResultSet query(String openCypher) {
    // parse openCypher


    // create execution plan
    // execute execution plan
    // populate ResultSet

    return new ResultSet();
  }
}
