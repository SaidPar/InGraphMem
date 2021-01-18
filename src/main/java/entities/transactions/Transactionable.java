package entities.transactions;

public interface Transactionable {
  void startTransaction();

  void commitTransaction();

  void abortTransaction();

  void rollBack();
}
