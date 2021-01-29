package entities.transactions;

import exceptions.TransactionException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/***
 * Manages transactions across the entire instance
 */
public final class TransactionManager {

  private static TransactionManager INSTANCE = new TransactionManager();
  private Map<Long, Transaction> transactions;
  AtomicLong tranID = new AtomicLong(0);

  private TransactionManager() {
    transactions = new ConcurrentHashMap<>();
  }

  public static TransactionManager getInstance() {
    return INSTANCE;
  }

  public Transaction createTransaction() {
    Transaction trans;
    synchronized (transactions) {
      Long newTranID = tranID.incrementAndGet();
      while (transactions.containsKey(newTranID)) {
        newTranID = tranID.incrementAndGet();
      }
      trans = new Transaction(newTranID);
      transactions.put(newTranID, trans);
    }

    return trans;
  }

  public Transaction getTransaction(Long txID) throws TransactionException {
    Transaction tx = transactions.get(txID);
    if (null == tx)
      throw new TransactionException("Transaction not found.");

    return tx;
  }
}
