package org.mcdealer.mcdealer;

import org.bukkit.scheduler.BukkitRunnable;

import org.python.util.PythonInterpreter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class executePython extends MCDealer {

    private static final Logger logger = LoggerFactory.getLogger("MCDealer");

    public executePython(BukkitRunnable executePython) {
        super();
    }

    void executePythonScript() {
        //Path to Python script in resources
        String scriptPath = "data-yml2json.py";

        try (InputStream scriptStream = getResource(scriptPath)) {
            if (scriptStream == null) {
                logger.warn("\n" + "Python script not found: " + scriptPath);
                return;
            }

            // Create a PythonInterpreter
            try (PythonInterpreter pythonInterpreter = new PythonInterpreter()) {
                //Run the Python script
                pythonInterpreter.exec("import sys");
                pythonInterpreter.exec("sys.path.append('lib/pyyaml-6.0.1/yaml)");
                pythonInterpreter.execfile(scriptStream);
            }
        } catch (Exception e) {
            logger.warn("Error executing Python script: " + e.getMessage());
        }
    }
}
