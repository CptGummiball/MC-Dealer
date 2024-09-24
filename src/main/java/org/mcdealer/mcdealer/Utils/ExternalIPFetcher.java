package org.mcdealer.mcdealer.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class ExternalIPFetcher {
    private static final Logger logger = LoggerFactory.getLogger("MCDealer");
    public static String getExternalIP() {
        try {
            URL url = new URL("http://checkip.amazonaws.com");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            return br.readLine().trim();
        } catch (IOException e) {
            logger.error("Error fetching external IP" + e.getMessage());
            return null;
        }
    }
}
