package query.parser;

import org.antlr.v4.runtime.tree.ParseTreeWalker;
import query.engine.optimizer.algebra.Path;
import query.engine.optimizer.algebra.PathDirection;
import query.engine.optimizer.algebra.PatternElement;
import query.parser.generated.CypherBaseListener;
import query.parser.generated.CypherParser;

public class ElementChainListener extends CypherBaseListener {

  private final Path path;

  private boolean hasRightArrow;
  private boolean hasLeftArrow;

  public ElementChainListener(Path path) {
    this.path = path;
  }

  @Override
  public void exitOC_RelationshipsPattern(CypherParser.OC_RelationshipsPatternContext ctx) {
    PathDirection direction = PathDirection.BOTH;

    if (hasRightArrow && hasLeftArrow)
      direction = PathDirection.BOTH;
    else if (hasRightArrow)
      direction = PathDirection.L_R;
    else if (hasLeftArrow)
      direction = PathDirection.R_L;

    path.setDirection(direction);
  }

  @Override
  public void enterOC_RightArrowHead(CypherParser.OC_RightArrowHeadContext ctx) {
    this.hasRightArrow = true;
  }

  @Override
  public void enterOC_LeftArrowHead(CypherParser.OC_LeftArrowHeadContext ctx) {
    this.hasLeftArrow = true;
  }

  @Override
  public void enterOC_NodePattern(CypherParser.OC_NodePatternContext ctx) {
    NameListener nameListener = new NameListener();
    ParseTreeWalker.DEFAULT.walk(nameListener, ctx);

    path.setNode2(new PatternElement(nameListener.getVariableName(), nameListener.getSymbolicName()));
  }

  @Override
  public void enterOC_RelationshipsPattern(CypherParser.OC_RelationshipsPatternContext ctx) {
    NameListener nameListener = new NameListener();
    ParseTreeWalker.DEFAULT.walk(nameListener, ctx);

    path.setRel(new PatternElement(nameListener.getVariableName(), nameListener.getSymbolicName()));
  }
}
