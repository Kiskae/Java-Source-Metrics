package nl.rug.jbi.jsm.frontend;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import nl.rug.jbi.jsm.core.JSMCore;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
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
            this.core.process(this, ImmutableList.of("com.shaboozey.agon.Constants$1"), new File("agon-1.0-SNAPSHOT.jar").toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processResult(final MetricResult result) {
        logger.debug(result);
    }
}
