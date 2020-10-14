package Archived;

import java.io.IOException;
import java.util.logging.*;

public class InteractionLogger {

    static Logger logger;
    public Handler fileHandler;
    Formatter plainText;

    private InteractionLogger() throws IOException {
        //instance the logger
        logger = Logger.getLogger(InteractionLogger.class.getName());
        //instance the filehandler
        fileHandler = new FileHandler("myLog.txt", true);
        //instance formatter, set formatting, and handler
        plainText = new SimpleFormatter();
        fileHandler.setFormatter(plainText);
        logger.addHandler(fileHandler);
    }

    private static Logger getLogger(){
        if(logger == null){
            try {
                new InteractionLogger();
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
