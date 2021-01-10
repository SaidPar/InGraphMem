package commands;

import common.Logger;
import database.types.DBOptions;
import database.types.DBType;
import database.Database;
import database.Instance;

public final class StartDBCommand implements Commandable {

    private static Logger logger = Logger.getLogger();

    private final String[] commands;

    // Switches
    private String name;
    private DBType storageOption;

    public StartDBCommand(String[] commands) {
        this.commands = commands;
    }

    public String getName() {
        return this.name;
    }

    public DBType getStorageOption() {
        return this.storageOption;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Commandable Interface Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parseOptions() {
        if (commands.length < 3) {
            logger.error("Invalid Options.");
            help();
            return;
        }

        for (int i = 1; i < commands.length; i++) {
            String option = commands[i];

            if (option.equalsIgnoreCase(CmdConsts.DB_NAME)) {
                this.name = commands[i + 1];
            } else if (option.equalsIgnoreCase(CmdConsts.DB_STORAGE)) {
                this.storageOption = parseStorageOption(commands[i + 1]);
            }
        }
    }

    @Override
    public boolean validateOptions() {
        if (null == name || name.isEmpty() || name.isBlank())
            return false;

        return true;
    }

    @Override
    public CmdOp getCommandType() {
        return CmdOp.START_DB;
    }

    @Override
    public void help() {
        logger.header("START DATABASE");
        logger.log(CmdConsts.DB_NAME);
        logger.log(CmdConsts.DB_STORAGE);

        // ToDo: Add descriptions
    }

    @Override
    public void execute() {
        try {

            DBOptions options = new DBOptions.Builder()
                    .withName(getName())
                    .storageType(getStorageOption())
                    .build();

            Instance instance = Instance.getInstance();
            instance.addDatabase(new Database(options));

            // Get db from the instance to ensure we have the tracked db.
            Database db = instance.getDatabase(getName());
            db.start();

        } catch (Exception e) {
            logger.error("An error occurred while starting the database: " + e.getMessage());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Helpers
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DBType parseStorageOption(String storageType) {
        if (storageType.equalsIgnoreCase(CmdConsts.IN_MEMORY))
            return DBType.IN_MEMORY;

        if (storageType.equalsIgnoreCase(CmdConsts.PORTABLE))
            return DBType.PORTABLE;

        return DBType.IN_MEMORY;
    }
}
