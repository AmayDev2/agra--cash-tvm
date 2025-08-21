package com.amay.tom.service.update;


import com.jcraft.jsch.*;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.function.BiConsumer;

public class SFTPDownloader {

    public static void downloadFile(String host, int port, String username, String password,
                                    String remoteFilePath, String localFilePath, BiConsumer<Double, String> progressCallback) {
        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(username, host, port);
            session.setPassword(password);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();

            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;


            // Download to temp path
            channelSftp.get(remoteFilePath, localFilePath, new ProgressMonitor(progressCallback));

            progressCallback.accept(1.0, "Download complete: " + localFilePath.replace("\\", "/"));
        } catch (Exception ex) {
            progressCallback.accept(0.0, "Error: " + ex.getMessage());
            throw new RuntimeException("Failed to download file from SFTP server: " + ex.getMessage(), ex);
        } finally {
            if (channelSftp != null) channelSftp.exit();
            if (channel != null) channel.disconnect();
            if (session != null) session.disconnect();
        }
    }
}
