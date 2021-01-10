package database;

public final class DBOptions {

    private DBType storageType;

    private DBOptions() {
    }

    public DBType getStorageType() {
        return storageType;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Builder
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class Builder {
        private final DBOptions INSTANCE;

        public Builder() {
            INSTANCE = new DBOptions();
        }

        public Builder storageType(DBType type) {
            this.INSTANCE.storageType = type;
            return this;
        }

        public DBOptions build() {
            return this.INSTANCE;
        }
    }
}
