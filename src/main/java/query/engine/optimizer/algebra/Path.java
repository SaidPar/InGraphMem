package query.engine.optimizer.algebra;

public final class Path {

  private PatternElement node1;
  private PatternElement rel;
  private PatternElement node2;
  private PathDirection dir;

  public Path() {
  }

  public void setNode1(PatternElement node1) {
    this.node1 = node1;
  }

  public void setRel(PatternElement rel) {
    this.rel = rel;
  }

  public void setNode2(PatternElement node2) {
    this.node2 = node2;
  }

  public void setDirection(PathDirection dir) {
    this.dir = dir;
  }
}
