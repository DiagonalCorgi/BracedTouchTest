package Drag;

import java.io.IOException;
import java.util.logging.*;

public class DragLogger {

    static Logger logger;
    public Handler fileHandler;
    Formatter plainText;

    /***
     * Logger class for the drag test. Outputs to dragLog.txt
     */

    private DragLogger() throws IOException {
        //instance the logger
        logger = Logger.getLogger(DragLogger.class.getName());
        //instance the filehandler
        fileHandler = new FileHandler("dragLog.txt", true);
        //instance formatter, set formatting, and handler
        plainText = new SimpleFormatter();
        fileHandler.setFormatter(plainText);
        logger.addHandler(fileHandler);
    }

    private static Logger getLogger(){
        if(logger == null){
            try {
                new DragLogger();
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
