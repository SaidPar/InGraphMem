package database;

public class PortableDatabase extends Database {

    public PortableDatabase(DBOptions options) {
        super(options);
    }

    @Override
    public void start() {
        super.start();
        // create file structure if needed?
    }
}
