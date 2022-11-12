package query.parser;

import query.parser.generated.CypherBaseListener;
import query.parser.generated.CypherParser;

public class NameListener extends CypherBaseListener {

  private String variableName;
  private String symbolicName;

  @Override
  public void enterOC_Variable(CypherParser.OC_VariableContext ctx) {
    variableName = ctx.getText();
  }

  @Override
  public void enterOC_SymbolicName(CypherParser.OC_SymbolicNameContext ctx) {
    symbolicName = ctx.getText();
  }

  public String getVariableName() {
    return this.variableName;
  }

  public String getSymbolicName() {
    return this.symbolicName;
  }
}
