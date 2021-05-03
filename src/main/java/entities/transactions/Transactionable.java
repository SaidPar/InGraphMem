package entities.transactions;

import exceptions.GraphException;

public interface Transactionable {
  void startTransaction();

  void commitTransaction() throws GraphException;

  void abortTransaction();

  void rollBack();
}
