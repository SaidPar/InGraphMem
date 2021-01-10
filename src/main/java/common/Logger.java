package common;

public final class Logger {

    private static Logger INSTANCE = new Logger();

    private Logger() {
    }

    public static Logger getLogger() {
        return INSTANCE;
    }

    public void log(String message) {
        System.out.println(ConsoleColors.YELLOW + message);
    }

    public void error(String errorMessage) {
        System.out.println(ConsoleColors.RED + errorMessage);
    }

    public void header(String header) {
        System.out.println(ConsoleColors.PURPLE + header);
    }

    public void inputCarrot() {
        System.out.print(ConsoleColors.BLUE_BOLD + "> ");
    }
}
