package database.types;

public final class DBOptions {

  private DBType storageType;
  private String name;

  private DBOptions() {
  }

  public DBType getStorageType() {
    return storageType;
  }

  public String getName() {
    return name;
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Builder
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public static class Builder {
    private final DBOptions OPTIONS;

    public Builder() {
      OPTIONS = new DBOptions();
    }

    public Builder storageType(DBType type) {
      OPTIONS.storageType = type;
      return this;
    }

    public Builder withName(String name) {
      OPTIONS.name = name;
      return this;
    }

    public DBOptions build() {

      if (null == OPTIONS.storageType)
        OPTIONS.storageType = DBType.IN_MEMORY;

      return OPTIONS;
    }
  }
}
