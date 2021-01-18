package entities.edit_options;

public final class InsertOptions extends EditOptions {

  public InsertOptions() {
    super();
  }

  public InsertOptions withTransactionID(Long txID) {
    this.setTransactionID(txID);
    return this;
  }
}

