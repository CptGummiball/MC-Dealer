package org.mcdealer.mcdealer;
import org.bukkit.scheduler.BukkitRunnable;
import org.python.util.PythonInterpreter;

import java.io.InputStream;

public class executePython extends MCDealer {

    public executePython(BukkitRunnable executePython) {
        super();
    }

    void executePythonScript() {
        //Path to Python script in resources
        String scriptPath = "data-yml2json.py";

        try (InputStream scriptStream = getResource(scriptPath)) {
            if (scriptStream == null) {
                getLogger().warning("\n" + "Python script not found: " + scriptPath);
                return;
            }

            // Create a PythonInterpreter
            try (PythonInterpreter pythonInterpreter = new PythonInterpreter()) {
                //Run the Python script
                pythonInterpreter.execfile(scriptStream);
            }
        } catch (Exception e) {
            getLogger().warning("Error executing Python script: " + e.getMessage());
        }
    }
}
