package exceptions;

public class InGraphDBException extends Exception {

  public InGraphDBException() {
    super();
  }

  public InGraphDBException(Throwable t) {
    super(t);
  }

  public InGraphDBException(String message, Throwable t) {
    super(message, t);
  }

  public InGraphDBException(String message) {
    super(message);
  }
}