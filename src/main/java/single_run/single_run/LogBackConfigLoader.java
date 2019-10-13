package single_run.single_run;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;

public class LogBackConfigLoader {

	/**
	 * 
	 * @param externalConfigFileLocation
	 * @throws Exception
	 */
    public static void load(String externalConfigFileLocation) throws Exception {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        lc.reset();
        configurator.doConfigure(externalConfigFileLocation);
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
    }
}
