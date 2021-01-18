package entities.edit_options;

public abstract class EditOptions {

  private Long transactionID;

  public EditOptions() {
  }

  public void setTransactionID(Long txID) {
    this.transactionID = txID;
  }

  public Long getTransactionID() {
    return this.transactionID;
  }
}

