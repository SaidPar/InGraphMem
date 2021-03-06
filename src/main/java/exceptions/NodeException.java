package exceptions;

public class NodeException extends GraphException {

  public NodeException() {
    super();
  }

  public NodeException(Throwable t) {
    super(t);
  }

  public NodeException(String message, Throwable t) {
    super(message, t);
  }

  public NodeException(String message) {
    super(message);
  }
}
