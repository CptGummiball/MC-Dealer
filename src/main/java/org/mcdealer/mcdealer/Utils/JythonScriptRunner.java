package org.mcdealer.mcdealer.Utils;

import org.python.core.PyException;
import org.python.core.PySystemState;
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

    public static void runPythonScript() {
        PythonInterpreter interpreter = new PythonInterpreter();

        try {
            // Pfade zu den Bibliotheken hinzufügen
            interpreter.exec("import sys");
            interpreter.exec("import os");

            // Hier wird der Pfad zum JAR-Verzeichnis gesetzt
            interpreter.exec("jar_directory = os.path.dirname(os.path.abspath(sys.argv[0]))");
            interpreter.exec("yaml_resource_path = '/yaml'");  // Ersetzen Sie '/yaml' durch den tatsächlichen Pfad
            interpreter.exec("yaml_full_path = os.path.join(jar_directory, yaml_resource_path)");
            interpreter.exec("sys.path.append(yaml_full_path)");

            // Laden Sie das Python-Skript aus der Ressourcen-Datei
            InputStream inputStream = JythonScriptRunner.class.getResourceAsStream("/data-yml2json-jython.py");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder scriptContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                scriptContent.append(line).append("\n");
            }

            // Führen Sie das Python-Skript im Jython-Interpreter aus
            interpreter.exec(scriptContent.toString());

            // Rufen Sie die Funktion "main" des Skripts auf, falls vorhanden
            interpreter.exec("if 'main' in globals():\n    main()");

        } catch (PyException | IOException e) {
            logger.error("Failed to run Python Script" + e.getMessage());
        } finally {
            // Call cleanup() in the finally block to ensure resources are properly released
            interpreter.cleanup();
        }
    }
}