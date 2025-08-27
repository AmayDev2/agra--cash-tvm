package com.amay.tom.service.update;


import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

            //Unzip the downloaded zip
//            unzip(localFilePath);

            progressCallback.accept(1.0, "Download complete: " + localFilePath.replace("\\", "/"));
        } catch (Exception ex) {
            ex.printStackTrace();
            progressCallback.accept(0.0, "Error: " + ex.getMessage());
            throw new RuntimeException("Failed to download file from SFTP server: " + ex.getMessage(), ex);
        } finally {
            if (channelSftp != null) channelSftp.exit();
            if (channel != null) channel.disconnect();
            if (session != null) session.disconnect();
        }
    }


        public static void unzip(String zipFilePath) {
            Path zipPath = Paths.get(zipFilePath);
            Path destDir = zipPath.getParent(); // same folder as .zip file

            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    Path newFilePath = destDir.resolve(entry.getName());

                    if (entry.isDirectory()) {
                        Files.createDirectories(newFilePath);
                    } else {
                        Files.createDirectories(newFilePath.getParent());
                        try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(newFilePath))) {
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                bos.write(buffer, 0, len);
                            }
                        }
                    }
                    zis.closeEntry();
                }

                Files.deleteIfExists(zipPath);
                System.out.println("Unzipped and deleted: " + zipFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}

