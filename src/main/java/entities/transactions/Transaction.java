package entities.transactions;

import java.util.HashMap;
import java.util.Map;

public class Transaction implements AutoCloseable {

  private TxStatus status = TxStatus.NOT_STARTED;
  private Map<String, Transactionable> participants;

  Transaction() {
    participants = new HashMap<>();
  }

  public void start() {
    // record time
    status = TxStatus.RUNNING;
  }

  public void commit() {
    // push work off to participant classes
    try {
      for (var participant : participants.entrySet()) {
        Transactionable txWorker = participant.getValue();
        txWorker.commitTransaction();
      }
    } catch (Exception e) {
      // ToDo: Rollback transaction across all participants
      // ToDo: Do I need a locking mechanism for inserts / updates / deletes
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