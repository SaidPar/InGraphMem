package entities.transactions;

import exceptions.NodeException;

public interface Transactionable {
  void startTransaction();

  void commitTransaction() throws NodeException;

  void abortTransaction();

  void rollBack();
}
