package entities.edit_options;

public final class DeleteOptions extends EditOptions {

  public DeleteOptions() {
    super();
  }

  public DeleteOptions withTransactionID(Long txID) {
    this.setTransactionID(txID);
    return this;
  }
}
