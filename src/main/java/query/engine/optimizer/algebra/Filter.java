package query.engine.optimizer.algebra;

import java.util.ArrayList;
import java.util.List;

public final class Filter {

  private final List<Predicate> andPredicates = new ArrayList<>();
  private final List<Predicate> orPredicates = new ArrayList<>();

  public void addAndPredicate(Predicate predicate) {
    andPredicates.add(predicate);
  }

  public void addOrPredicate(Predicate predicate) {
    orPredicates.add(predicate);
  }
}
