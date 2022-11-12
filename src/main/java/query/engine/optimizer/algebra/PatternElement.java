package query.engine.optimizer.algebra;

public class PatternElement {

  private final String varName;
  private final String name;

  public PatternElement(String varName, String name) {
    this.varName = varName;
    this.name = name;
  }

}
