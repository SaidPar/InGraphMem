package entities.edit_options;

public final class UpdateOptions extends EditOptions {

  boolean returnOld = false;
  boolean returnNew = false;

  public UpdateOptions() {
    super();
  }

  public UpdateOptions withTransactionID(Long txID) {
    this.setTransactionID(txID);
    return this;
  }

  public UpdateOptions returnOld() {
    this.returnOld = true;
    return this;
  }

  public UpdateOptions returnNew() {
    this.returnNew = true;
    return this;
  }

  public boolean canReturnOld() {
    return this.returnOld;
  }

  public boolean canReturnNew() {
    return this.returnNew;
  }
}
