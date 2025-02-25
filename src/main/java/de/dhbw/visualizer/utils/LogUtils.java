package de.dhbw.visualizer.utils;

import java.util.Date;
import java.util.logging.*;

public class LogUtils {

    public static void setup() {
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.INFO);
        logger.setUseParentHandlers(false);

        var formatter = new MyLogFormatter();

        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        logger.addHandler(consoleHandler);
    }

    private static class MyLogFormatter extends SimpleFormatter {

        private static final String FORMAT = "[%1$tF %1$tT.%1$tL] [%2$-7s] %3$s %n";

        @Override
        public String format(LogRecord record) {
            return String.format(FORMAT, new Date(record.getMillis()), record.getLevel().getLocalizedName(), record.getMessage());
        }
    }

    private LogUtils() {
        // ignored
    }
}
