import common.Constants;
import common.Logger;

import java.util.Scanner;

public class Entry {

    private static final Logger logger = Logger.getLogger();

    public static void main(String[] args) {

        printIntro();

        Scanner input = new Scanner(System.in);
        try {
            boolean canExit = false;
            while (!canExit) {
                logger.inputCarrot();
                canExit = processCommand(input.nextLine());
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            input.close();
        }
    }

    private static void printIntro() {
        logger.header("InGraphMemDB");
        logger.log("------------------------");
    }

    private static boolean processCommand(String input) {

        if (input.equalsIgnoreCase(Constants.EXIT))
            return true;

        if (input.startsWith(Constants.START_DATABASE)) {

        }

        return false;
    }
}
