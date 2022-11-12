package query.engine.optimizer.algebra;

import java.util.ArrayList;
import java.util.List;

public final class GraphExpression {

  private final List<Path> paths = new ArrayList<>();
  private final List<Filter> filters = new ArrayList<>();
  private final List<Projection> projections = new ArrayList<>();

  public GraphExpression() {
  }

  public void addPath(Path path) {
    paths.add(path);
  }

  public void addFilter(Filter filter) {
    filters.add(filter);
  }

  public void addProjection(Projection projection) {
    projections.add(projection);
  }
}
