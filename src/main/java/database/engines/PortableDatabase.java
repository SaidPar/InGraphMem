package database.engines;

public class PortableDatabase implements DBEngine {

  public PortableDatabase() {
  }

  @Override
  public void start() {
    // create file structure if needed?
  }

  @Override
  public void stop() {
    // persist
  }
}
