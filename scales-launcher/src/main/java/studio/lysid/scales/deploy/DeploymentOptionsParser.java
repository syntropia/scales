package studio.lysid.scales.deploy;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;

class DeploymentOptionsParser {

    private static final Logger logger = LoggerFactory.getLogger(DeploymentOptionsParser.class);

    private static final String verticleDeploymentOptionsJsonFileName = "scales-deploy-config.json";

    /**
     * Try to parse a JSON file named <code>scales-deploy-config.json</code> from the classpath,
     * that should contain Vert.x deployment options for one or more verticles.
     * This file should contain a single Object with one property for each verticle to be deployed and configured.
     * That property contains an object describing the verticle deployment options as defined by the
     * <code>DeploymentOptions.fromJson(JsonObject)</code> Vert.x method.
     *
     * <p>The verticle names to used as properties of the main object
     * are defined in the <code>shortName</code> property of the <code>Verticle</code> enumeration.</p>
     *
     * <p>If the JSON file exists, the following rules will apply :
     * <ul>
     *     <li>Verticles for which no configuration is provided <strong>will NOT be deployed</strong>.
     *     This is a deprecated way of not deploying a verticle, please prefer the last option.</li>
     *     <li>Verticles for which a property exists but does not contain
     *     a DeploymentOptions configuration object will be
     *     <strong>deployed with Vert.x default</strong> deployment options.</li>
     *     <li>Verticles for which a property exists containing a valid DeploymentOptions configuration object
     *     will use these options for their deployment. <br/>
     *     You can provide only a subset of the options, the others will use the Vert.x default values.
     *     More specifically, you can set <code>"instances":0</code> to disable deployment of the verticle.</li>
     * </ul>
     * </p>
     *
     * <p>Some verticles have additional parameters that can be configured in their "config" property.
     * This property should contain an object with parameters as key/value pairs. For example,
     * you can configure the HTTP listening port for the "facade" verticle like this :
     * <pre><code>
     * {
     *     "facade" : {
     *         "instances" : 1,
     *         "config" : {
     *             "http.port" : 8080
     *         }
     *     }
     * }
     * </code></pre>
     * </p>
     *
     * @see Verticle#shortName
     * @see io.vertx.core.DeploymentOptions#fromJson(JsonObject)
     */
    static void parseVerticleDeploymentOptionsJsonFile() {

        JsonObject verticlesDeploymentOptions = getVerticlesDeploymentOptionsFromConfigFile();
        if (verticlesDeploymentOptions != null) {
            logger.info("Loading verticle deployment options from JSON file: {0}", verticleDeploymentOptionsJsonFileName);
            parseConfigForVerticle(verticlesDeploymentOptions, Verticle.EventStore);
            parseConfigForVerticle(verticlesDeploymentOptions, Verticle.Command);
            parseConfigForVerticle(verticlesDeploymentOptions, Verticle.Query);
            parseConfigForVerticle(verticlesDeploymentOptions, Verticle.Facade);
        } else {
            logger.info("No JSON files provided in classpath. Using Vert.x default deployment options for all verticles.");
        }

        initializeFacadeDefaultOptions();
    }

    private static JsonObject getVerticlesDeploymentOptionsFromConfigFile() {
        JsonObject verticlesDeploymentOptions = null;
        try {
            String jsonFileContents = new String(Files.readAllBytes(Paths.get(verticleDeploymentOptionsJsonFileName)));
            verticlesDeploymentOptions = new JsonObject(jsonFileContents);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        return verticlesDeploymentOptions;
    }

    private static void parseConfigForVerticle(JsonObject verticlesDeploymentOptions, Verticle verticle) {
        JsonObject customVerticleOptions = verticlesDeploymentOptions.getJsonObject(verticle.shortName);
        if (customVerticleOptions != null) {
            verticle.deploymentOptions.fromJson(customVerticleOptions);
            logger.info("Deployment options parsed for verticle [{0}]. {1} instance(s) will be deployed.", verticle.shortName, verticle.deploymentOptions.getInstances());
        } else {
            verticle.deploymentOptions.setInstances(0);
            logger.warn("Deployment options NOT FOUND for verticle [{0}], it will NOT be deployed. Prefer setting options for all verticles and use \"instances\":0 for those you don't want to deploy.", verticle.shortName);
        }
    }

    private static void initializeConfigOptionDefaultValue(JsonObject config, String key, Integer defaultValue) {
        if (!config.containsKey(key)) {
            config.put(key, defaultValue);
        }
    }

    private static void initializeFacadeDefaultOptions() {
        JsonObject config = Verticle.Facade.deploymentOptions.getConfig();
        if (config == null) {
            config = new JsonObject();
            Verticle.Facade.deploymentOptions.setConfig(config);
        }
        initializeConfigOptionDefaultValue(config, "http.port", 8080);
    }

}
