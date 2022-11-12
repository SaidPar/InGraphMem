package query.engine.optimizer;

import org.antlr.v4.runtime.tree.ParseTreeWalker;
import query.engine.optimizer.algebra.GraphExpression;
import query.parser.GraphExpressionListener;
import query.parser.generated.CypherParser;

public final class QueryOptimizer {

  private final IQueryPlanner planner;
  private final CypherParser.OC_CypherContext parseTree;

  public QueryOptimizer(IQueryPlanner planner, CypherParser.OC_CypherContext parseTree) {
    this.planner = planner;
    this.parseTree = parseTree;
  }

  public GraphExpression getExecutionPlan() {

    // generate graph algebra

    GraphExpressionListener graphExpListener = new GraphExpressionListener();
    ParseTreeWalker.DEFAULT.walk(graphExpListener, parseTree);

    GraphExpression graphExpression = graphExpListener.getGraphExpression();

    // ask planner to optimize execution tree
    // GraphExpression optimizedExpr = planner.optimizeExpression(graphExpression);

    //return optimizedExpr;
    return graphExpression;
  }

}
