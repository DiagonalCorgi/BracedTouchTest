package Rotation;

import java.io.IOException;
import java.util.logging.*;

public class RotationLogger {

    static Logger logger;
    public Handler fileHandler;
    Formatter plainText;

    /***
     * Logger class for the rotation test. Outputs to rotationLog.txt
     */
    private RotationLogger() throws IOException {
        //instance the logger
        logger = Logger.getLogger(RotationLogger.class.getName());
        //instance the filehandler
        fileHandler = new FileHandler("rotationLog.txt", true);
        //instance formatter, set formatting, and handler
        plainText = new SimpleFormatter();
        fileHandler.setFormatter(plainText);
        logger.addHandler(fileHandler);
    }

    private static Logger getLogger(){
        if(logger == null){
            try {
                new RotationLogger();
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

