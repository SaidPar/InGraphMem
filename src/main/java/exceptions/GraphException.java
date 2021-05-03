package exceptions;

public class GraphException extends Exception {
  public GraphException() {
    super();
  }

  public GraphException(Throwable t) {
    super(t);
  }

  public GraphException(String message, Throwable t) {
    super(message, t);
  }

  public GraphException(String message) {
    super(message);
  }
}
