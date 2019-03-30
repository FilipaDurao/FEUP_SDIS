package proj.peer.log;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkLogger extends Logger {
    private static NetworkLogger ourInstance = new NetworkLogger();

    public static NetworkLogger getInstance() {
        return ourInstance;
    }

    private NetworkLogger() {
        super("NetworkLogger", null);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new LogFormatter());
        this.addHandler(handler);
    }

    public static void printLog(Level level, String msg) {
        NetworkLogger.getInstance().log(level, msg);
    }

    public static void printLog(Level level, String msg, Object[] params) {
        NetworkLogger.getInstance().log(level, msg, params);
    }

    public static void printLog(Level level, String msg, Object param) {
        NetworkLogger.getInstance().log(level, msg, param);
    }
}
