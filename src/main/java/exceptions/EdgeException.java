package exceptions;

public class EdgeException extends GraphException {
  public EdgeException() {
    super();
  }

  public EdgeException(Throwable t) {
    super(t);
  }

  public EdgeException(String message, Throwable t) {
    super(message, t);
  }

  public EdgeException(String message) {
    super(message);
  }
}
