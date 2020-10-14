package Scale;

import java.io.IOException;
import java.util.logging.*;

public class ScaleLogger {

    static Logger logger;
    public Handler fileHandler;
    Formatter plainText;

    /***
     * Logger class for the scale test. Outputs to scaleLog.txt
     */
    private ScaleLogger() throws IOException {
        //instance the logger
        logger = Logger.getLogger(ScaleLogger.class.getName());
        //instance the filehandler
        fileHandler = new FileHandler("scaleLog.txt", true);
        //instance formatter, set formatting, and handler
        plainText = new SimpleFormatter();
        fileHandler.setFormatter(plainText);
        logger.addHandler(fileHandler);
    }

    private static Logger getLogger(){
        if(logger == null){
            try {
                new ScaleLogger();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return logger;
    }
    public static void log(Level level, String msg){
        getLogger().log(level, msg);
        System.out.println(msg);
    }
}
