package org.mcdealer.mcdealer;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.python.util.PythonInterpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;

public class TaskScheduler extends MCDealer {

    private int UpdateInterval;

    public TaskScheduler(MCDealer plugin) {
    }

    void loadConfig() {
        // Load the configuration from config.yml
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        UpdateInterval = config.getInt("Update-Interval", 300);
    }

    Runnable scheduleRepeatingTask() {
        RepeatingTask repeatingTask = new RepeatingTask();
        repeatingTask.runTaskTimer(this, 0L, UpdateInterval * 20L);
        return repeatingTask;
    }

    // Inner class for the recurring task
    private class RepeatingTask extends BukkitRunnable {

        public void run() {
            // Code to run every 'UpdateInterval'
            getLogger().info(" Collect data for MCDealer ");
            getLogger().info(" Jython running... ");

            // Path to Python script
            String pythonScriptPath = getDataFolder() + File.separator + "plugins" + File.separator + "MCDealer" + File.separator + "data-yml2json.py";

            // Execute Python script
            try {
                try (PythonScriptExecutor executor = new PythonScriptExecutor(pythonScriptPath)) {
                    // Do nothing here; the script is already executed in the constructor
                }
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, " An error occurred while executing the Python script", e);
            }

            getLogger().info(" Data collected ");
        }

        private static class PythonScriptExecutor implements AutoCloseable {
            private final PythonInterpreter pythonInterpreter;

            public PythonScriptExecutor(String scriptPath) throws IOException {
                this.pythonInterpreter = new PythonInterpreter();
                BufferedReader reader = new BufferedReader(new FileReader(scriptPath));
                String line;
                StringBuilder scriptContent = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    scriptContent.append(line).append("\n");
                }
                pythonInterpreter.exec(scriptContent.toString());
            }

            @Override
            public void close() {
                // Close resources associated with PythonInterpreter
                pythonInterpreter.close();
            }
        }
    }
}