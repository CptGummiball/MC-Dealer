package org.mcdealer.mcdealer.Utils;

import org.python.core.PyException;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JythonScriptRunner {

    private static final Logger logger = LoggerFactory.getLogger("MCDealer");
    public static void main(String[] args) {
        runPythonScript();
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static void runPythonScript() {
        PythonInterpreter interpreter = new PythonInterpreter();

        try {

            interpreter.exec("import sys");
            interpreter.exec("import os");


            interpreter.exec("jar_directory = os.path.dirname(os.path.abspath(sys.argv[0]))");
            interpreter.exec("yaml_resource_path = '/yaml'");
            interpreter.exec("yaml_full_path = os.path.join(jar_directory, yaml_resource_path)");
            interpreter.exec("sys.path.append(yaml_full_path)");

            interpreter.exec("nbtlib_resource_path = '/nbtlib'");
            interpreter.exec("nbtlib_full_path = os.path.join(jar_directory, nbtlib_resource_path)");
            interpreter.exec("sys.path.append(nbtlib_full_path)");


            InputStream inputStream = JythonScriptRunner.class.getResourceAsStream("/data-yml2json-jython.py");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder scriptContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                scriptContent.append(line).append("\n");
            }


            interpreter.exec(scriptContent.toString());


            interpreter.exec("if 'main' in globals():\n    main()");

        } catch (PyException | IOException e) {
            logger.error("Failed to run Python Script. Error details: {}", e.getMessage());
        } finally {

            interpreter.cleanup();
        }
    }
}
