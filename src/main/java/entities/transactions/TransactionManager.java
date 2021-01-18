package entities.transactions;

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
    Transaction trans = new Transaction();
    synchronized (transactions) {
      Long newTranID = tranID.incrementAndGet();
      while (transactions.containsKey(newTranID)) {
        newTranID = tranID.incrementAndGet();
      }
      transactions.put(newTranID, trans);
    }

    return trans;
  }

  public Transaction getTransaction(Long txID) throws Exception {
    Transaction tx = transactions.get(txID);
    if (null == tx)
      throw new Exception("Transaction not found."); // ToDo: TransactionException

    return tx;
  }
}
