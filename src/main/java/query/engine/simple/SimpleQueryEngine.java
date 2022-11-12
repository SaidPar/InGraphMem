package query.engine.simple;

import common.Logger;
import database.Database;
import query.engine.IQueryEngine;
import query.engine.ResultSet;
import query.engine.optimizer.QueryOptimizer;
import query.engine.optimizer.algebra.GraphExpression;
import query.engine.optimizer.rule.planner.RulePlanner;
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
      QueryOptimizer optimizer = new QueryOptimizer(new RulePlanner(), parseTree);
      GraphExpression executionPlan = optimizer.getExecutionPlan();

      // execute execution plan
      // populate ResultSet

      return new ResultSet();
    } catch (Exception e) {
      logger.error(e.getMessage());
      return new ResultSet();
    }
  }
}
