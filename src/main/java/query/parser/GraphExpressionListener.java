package query.parser;

import org.antlr.v4.runtime.tree.ParseTreeWalker;
import query.engine.optimizer.algebra.GraphExpression;
import query.engine.optimizer.algebra.Path;
import query.engine.optimizer.algebra.PatternElement;
import query.parser.generated.CypherBaseListener;
import query.parser.generated.CypherParser;

import java.util.*;


public class GraphExpressionListener extends CypherBaseListener {

  private final GraphExpression graphExpression;
  private final Deque<Path> paths = new ArrayDeque<>();

  public GraphExpressionListener() {
    this.graphExpression = new GraphExpression();
  }

  public GraphExpression getGraphExpression() {
    return this.graphExpression;
  }

  @Override
  public void exitOC_Cypher(CypherParser.OC_CypherContext ctx) {
    for (var path : paths) {
      graphExpression.addPath(path);
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Paths
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public void enterOC_PatternElement(CypherParser.OC_PatternElementContext ctx) {
    var s = ctx.children;
    // ToDo: Handle multiple children, correctly identify all path parts (they may be shared)
    // ()-[]->()<-[]-()

    Path path = new Path();
    paths.add(path);
  }

  @Override
  public void enterOC_NodePattern(CypherParser.OC_NodePatternContext ctx) {
    NameListener nameListener = new NameListener();
    ParseTreeWalker.DEFAULT.walk(nameListener, ctx);

    paths.getLast()
      .setNode1(new PatternElement(nameListener.getVariableName(), nameListener.getSymbolicName()));
  }

  @Override
  public void enterOC_PatternElementChain(CypherParser.OC_PatternElementChainContext ctx) {
    ElementChainListener chainListener = new ElementChainListener(paths.getLast());
    ParseTreeWalker.DEFAULT.walk(chainListener, ctx);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Filters
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public void enterOC_Where(CypherParser.OC_WhereContext ctx) {

  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Projections
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public void enterOC_Return(CypherParser.OC_ReturnContext ctx) {

  }
}
