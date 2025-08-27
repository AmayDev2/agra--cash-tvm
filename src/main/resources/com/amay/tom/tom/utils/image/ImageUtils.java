package com.amay.tom.utils.image;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtils {

    public static void saveImage(BufferedImage image, String outputPath, String format) throws IOException {
        File outputFile = new File(outputPath);
        try {
            ImageIO.write(image, format, outputFile);
            System.out.println("Image saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static BufferedImage nodeToImage(Node node) {
//        // Create a WritableImage from the Node
//        WritableImage writableImage = node.snapshot(new SnapshotParameters(), null);
//
//        // Convert the WritableImage to a BufferedImage
//        return SwingFXUtils.fromFXImage(writableImage, null);
//    }

    public static BufferedImage nodeToImage(Node node) {
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(javafx.scene.paint.Color.TRANSPARENT);
        params.setTransform(javafx.scene.transform.Transform.scale(5, 5));

        WritableImage writableImage = node.snapshot(params, null);
        return SwingFXUtils.fromFXImage(writableImage, null);
    }

//public static BufferedImage nodeToImage(Node node) {
//    // Create a SnapshotParameters object to specify image properties
//    SnapshotParameters params = new SnapshotParameters();
//
//    // Set the parameters to capture the best resolution possible
//    params.setFill(javafx.scene.paint.Color.TRANSPARENT);
//    params.setDepthBuffer(true);
//    params.setViewport(null); // Set viewport to null to capture the entire scene
//
//    // Create a WritableImage from the Node
//    WritableImage writableImage = node.snapshot(params, null);
//
//    // Convert the WritableImage to a BufferedImage
//    return SwingFXUtils.fromFXImage(writableImage, null);
//}

    public static void saveBufferedImage(BufferedImage image, String outputPath) {
        File outputFile = new File(outputPath);
        try {
            ImageIO.write(image, "png", outputFile);
            System.out.println("Image saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static BufferedImage findTicket(String ticketId) {
        ticketId="1711084387192";
        if(ticketId!=null){
            File file=new File("../../../tickets");
            File[] files=file.listFiles();
            assert files != null;
            for(File f:files){
                if(f.isDirectory()){
                    File[] files1=f.listFiles();
                    assert files1 != null;
                    for(File f1:files1){
                        if(f1.getName().equals(ticketId+".png")){
                            try {
                                return ImageIO.read(f1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }


            }
        }


        return null;
    }
}
