//package com.amay.tom.grpc;
//
//import com.google.protobuf.ByteString;
//import io.grpc.stub.StreamObserver;
//import org.junit.jupiter.api.Test;
//import org.unitral.module.DownloadRequest;
//import org.unitral.module.FileChunk;
//import org.unitral.module.FtpServiceGrpc;
//import org.unitral.module.UploadResponse;
//
//import java.io.*;
//import java.time.Duration;
//import java.time.Instant;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//public class FtpServiceTest {
//
//    @Test
//    public void testUploadFile() throws IOException {
//        Instant start = Instant.now();
//
//        final boolean[] flag = {true};
//
//        // Create gRPC stub
//        FtpServiceGrpc.FtpServiceStub stub = GrpcConfig.getFileAsyncStub();
//        String filePath = "C:\\Users\\risab\\Downloads\\AxonServer.zip";
//        File file = new File(filePath);
//        long fileSize = file.length();
//
//        // Create a StreamObserver to handle the response from the server
//        StreamObserver<UploadResponse> responseObserver = new StreamObserver<UploadResponse>() {
//            @Override
//            public void onNext(UploadResponse value) {
//                assertTrue(value.getSuccess());
//            }
//
//            @Override
//            public void onError(Throwable t) {
//                t.printStackTrace();
//            }
//
//            @Override
//            public void onCompleted() {
//                Instant end = Instant.now();
//                Duration duration = Duration.between(start, end);
//                double uploadTimeSeconds = duration.toMillis() / 1000.0;
//
//                // Calculate upload speed
//                double speedMBps = fileSize / (1024.0 * 1024.0) / uploadTimeSeconds;
//                System.out.printf("Upload speed: %.2f MB per second\n", speedMBps);
//
//                flag[0] = false;
//            }
//        };
//
//        // Call the uploadFile method asynchronously
//        StreamObserver<FileChunk> requestObserver = stub.uploadFile(responseObserver);
//        int bytesReadTotal = 0;
//
//        try (InputStream inputStream = new FileInputStream(filePath)) {
//            byte[] buffer = new byte[1024*1024];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                bytesReadTotal += buffer.length;
//                ByteString byteString = ByteString.copyFrom(buffer, 0, bytesRead);
//                FileChunk fileChunk = FileChunk.newBuilder().setChunk(byteString).build();
//                requestObserver.onNext(fileChunk);
//
//                // Calculate remaining file size
//                long remainingBytes = fileSize - bytesReadTotal;
//                double remainingSizeMB = (double) remainingBytes / (1024.0 * 1024.0);
//                System.out.printf("Remaining file size: %.2f MB\n", remainingSizeMB);
//
//                // Wait for 5 seconds
////                try {
////                    Thread.sleep(1000);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
//            }
//        }
//
//        // Notify the server that the file upload is complete
//        requestObserver.onCompleted();
//
//        // Wait until the upload completes
//        while (flag[0]) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//    @Test
//    public void testDownloadFile() throws IOException {
//        Instant start = Instant.now();
//        boolean[] flag = {true};
//
//        // Create gRPC stub
//        FtpServiceGrpc.FtpServiceStub stub = GrpcConfig.getFileAsyncStub();
//
//        final String filename= "postgresql-16.2-1-windows-x64 (1).exe";
//
//        final int[] filesize = {0};
//
//        // Create a StreamObserver to handle the response from the server
//        StreamObserver<FileChunk> responseObserver = new StreamObserver<FileChunk>() {
//            FileOutputStream outputStream = new FileOutputStream("E:\\Testfiles\\"+filename);
//
//            @Override
//            public void onNext(FileChunk value) {
//                try {
//                    filesize[0] +=value.getChunk().size()/(1024*1024);
//                    System.out.println("Writing chunk to file "+value.getChunk().size()/(1024*1024));
//                    System.out.println("Writing chunk to file "+ filesize[0] );
//                    outputStream.write(value.getChunk().toByteArray());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(Throwable t) {
//                t.printStackTrace();
//            }
//
//            @Override
//            public void onCompleted() {
//                Instant end = Instant.now();
//                Duration duration = Duration.between(start, end);
//                double downloadTimeSeconds = duration.toMillis() / 1000.0;
//                flag[0] = false;
//                System.out.println("Total size: "+filesize[0]);
//                System.out.printf("Download time: %.2f seconds\n", downloadTimeSeconds);
//
//                // Close the output stream when download completes
//                try {
//                    outputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//
//        // Call the downloadFile method
//        stub.downloadFile(DownloadRequest.newBuilder().setFileName(filename).build(),responseObserver);
//
//        // Wait for the download to complete
//        try {
//            while (flag[0]) Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
