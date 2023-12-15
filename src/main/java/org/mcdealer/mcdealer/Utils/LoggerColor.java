package org.mcdealer.mcdealer.Utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;

public class LoggerColor extends LayoutBase<ILoggingEvent> {

    @Override
    public String doLayout(ILoggingEvent event) {
        Level level = event.getLevel();
        String color = switch (level.toInt()) {
            case Level.INFO_INT -> "\u001B[32m"; // Green fpr INFO
            case Level.WARN_INT -> "\u001B[33m"; // Yellow for WARN
            case Level.ERROR_INT -> "\u001B[31m"; // RED for ERROR
            default -> "\u001B[0m"; // Default color
        };

        // Set colors based on log level

        //Return log message with the appropriate color
        return color + event.getFormattedMessage() + "\u001B[0m";
    }
}