package ma.shopflow.acceptation;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.JUNIT_PLATFORM_NAMING_STRATEGY_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

/**
 * Lance tous les scénarios Cucumber du dossier
 * src/test/resources/fonctionnalites.
 */
@Suite
@IncludeEngines("cucumber")
@SelectPackages("fonctionnalites")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "ma.shopflow.acceptation")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber/rapport.html, json:target/cucumber/rapport.json")
@ConfigurationParameter(key = JUNIT_PLATFORM_NAMING_STRATEGY_PROPERTY_NAME, value = "long")
public class AcceptationIT {
}
