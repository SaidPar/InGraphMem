package entities.transactions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transaction implements AutoCloseable {

  private final Long txID;
  private TxStatus status = TxStatus.NOT_STARTED;
  private Map<String, Transactionable> participants;

  Transaction(Long txID) {
    this.txID = txID;
    participants = new HashMap<>();
  }

  public Long getID() {
    return txID;
  }

  public void start() {
    // record time
    status = TxStatus.RUNNING;
  }

  public void commit() {
    // push work off to participant classes
    List<Transactionable> committed = new ArrayList<>();
    try {
      for (var participant : participants.entrySet()) {
        Transactionable txWorker = participant.getValue();
        txWorker.commitTransaction();
        committed.add(txWorker);
      }
    } catch (Exception e) {
      // Rollback transaction across all committed participants
      // ToDo: Do I need a locking mechanism for inserts / updates / deletes
      for (Transactionable txWorker : committed) {
        txWorker.rollBack();
      }

      status = TxStatus.ABORTED;
      return;
    }

    status = TxStatus.COMMITTED;
    // ToDo: remove transaction from transaction manager?
    //  subscription model?
  }

  public void abort() {
    // push work off to participant classes
    status = TxStatus.ABORTED;
  }

  public TxStatus getStatus() {
    return this.status;
  }

  public Transactionable getParticipant(String name) throws Exception {
    Transactionable participant = participants.get(name);
    return participant;
  }

  public void addParticipant(String name, Transactionable participant) {
    synchronized (participants) {
      if (!participants.containsKey(name)) {
        participants.put(name, participant);
      }
    }
  }

  @Override
  public void close() throws Exception {
    // close participant classes resources
  }
}
