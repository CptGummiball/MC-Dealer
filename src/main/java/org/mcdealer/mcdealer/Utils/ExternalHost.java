package org.mcdealer.mcdealer.Utils;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.bukkit.plugin.java.JavaPlugin;
import org.cptgum.simpleftpsync.FTPUtil;
import org.cptgum.simpleftpsync.FTPSUtil;
import org.cptgum.simpleftpsync.SFTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;


public class ExternalHost {
    private static final Logger logger = LoggerFactory.getLogger("MCDealer");
    private final JavaPlugin plugin;

    public ExternalHost(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void run() throws IOException, JSchException, SftpException {
        String type = plugin.getConfig().getString("ExternalHost.type");
        String server = plugin.getConfig().getString("ExternalHost.server");
        int port = plugin.getConfig().getInt("ExternalHost.port");
        String user = plugin.getConfig().getString("ExternalHost.username");
        String pass = plugin.getConfig().getString("ExternalHost.password");
        String localPath1 = "/plugins/MCDealer/web/output.json";
        String localPath2 = "/plugins/MCDealer/web/hidden_shops.json";
        String remotePath = plugin.getConfig().getString("ExternalHost.remote-path");
        String shkc = plugin.getConfig().getString("ExternalHost.StrictHostKeyChecking");

        if (Objects.equals(type, "FTP")) {
            FTPUtil.uploadFile(server, port, user, pass, remotePath, localPath1);
            FTPUtil.uploadFile(server, port, user, pass, remotePath, localPath2);
            logger.info("External Host updated!");
        } else if (Objects.equals(type, "SFTP")) {
            SFTPUtil.uploadFile(server, port, user, pass, shkc, remotePath, localPath1);
            SFTPUtil.uploadFile(server, port, user, pass, shkc, remotePath, localPath2);
            logger.info("External Host updated!");
        } else if (Objects.equals(type, "FTPS")) {
            FTPSUtil.uploadFile(server, port, user, pass, remotePath, localPath1);
            FTPSUtil.uploadFile(server, port, user, pass, remotePath, localPath2);
            logger.info("External Host updated!");
        }else{
            logger.error("Invalid ExternalHost.type");
            logger.error("Nothing was uploaded");
        }
    }
    public void uploadall() throws IOException, JSchException, SftpException {
        String type = plugin.getConfig().getString("ExternalHost.type");
        String server = plugin.getConfig().getString("ExternalHost.server");
        int port = plugin.getConfig().getInt("ExternalHost.port");
        String user = plugin.getConfig().getString("ExternalHost.username");
        String pass = plugin.getConfig().getString("ExternalHost.password");
        String localPath3 = "/plugins/MCDealer/web";
        String remotePath = plugin.getConfig().getString("ExternalHost.remote-path");
        String shkc = plugin.getConfig().getString("ExternalHost.StrictHostKeyChecking");

        if (Objects.equals(type, "FTP")) {
            FTPUtil.uploadFile(server, port, user, pass, remotePath, localPath3);
            logger.info("External Host updated!");
        } else if (Objects.equals(type, "SFTP")) {
            SFTPUtil.uploadFile(server, port, user, pass, shkc, remotePath, localPath3);
            logger.info("External Host updated!");
        } else if (Objects.equals(type, "FTPS")) {
            FTPSUtil.uploadFile(server, port, user, pass, remotePath, localPath3);
            logger.info("External Host updated!");
        }else{
            logger.error("Invalid ExternalHost.type");
            logger.error("Nothing was uploaded");
        }
    }

}
