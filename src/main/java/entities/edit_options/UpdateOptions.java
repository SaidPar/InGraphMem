package entities.edit_options;

public final class UpdateOptions extends EditOptions {

  public UpdateOptions() {
    super();
  }

  public UpdateOptions withTransactionID(Long txID) {
    this.setTransactionID(txID);
    return this;
  }
}
