package entities.edit_options;

public final class DeleteOptions extends EditOptions {

  private boolean cascadeDeletes;

  public DeleteOptions() {
    super();
  }

  public DeleteOptions withTransactionID(Long txID) {
    this.setTransactionID(txID);
    return this;
  }

  // ToDo: Implement Cascade Delete
  public DeleteOptions withCascadeDeletes(boolean canCascadeDelete) {
    this.cascadeDeletes = canCascadeDelete;
    return this;
  }
}
