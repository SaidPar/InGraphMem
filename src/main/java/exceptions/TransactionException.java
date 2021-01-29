package exceptions;

public class TransactionException extends Exception {
  public TransactionException() {
    super();
  }

  public TransactionException(Throwable t) {
    super(t);
  }

  public TransactionException(String message, Throwable t) {
    super(message, t);
  }

  public TransactionException(String message) {
    super(message);
  }
}
