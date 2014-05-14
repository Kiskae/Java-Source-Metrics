package nl.rug.jbi.jsm.frontend;

import com.google.common.base.Preconditions;
import nl.rug.jbi.jsm.core.JSMCore;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.util.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class DebugFrontend implements Frontend {
    private final static Logger logger = LogManager.getLogger(DebugFrontend.class);
    private final JSMCore core;

    public DebugFrontend(final JSMCore core) {
        this.core = Preconditions.checkNotNull(core);
    }

    @Override
    public void init() {
        try {
            final File file = new File("agon-1.0-SNAPSHOT.jar");
            this.core.process(this, FileUtils.findClassNames(file), file.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processResult(final MetricResult result) {
        logger.debug(result);
    }
}
