package nl.rug.jbi.jsm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bootstrap {
    private static final Logger logger = LogManager.getLogger(Bootstrap.class);

    public static void main(final String[] args) {
        logger.trace("Starting Application");
        logger.trace("Exiting Application");
    }
}
