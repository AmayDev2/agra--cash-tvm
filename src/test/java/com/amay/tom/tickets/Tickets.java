//package com.amay.tom.tickets;
//
//import com.amay.tom.ViewFactory;
//import com.amay.tom.utils.folder.NewFolder;
//import com.amay.tom.utils.image.ImageUtils;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.layout.Pane;
//import org.junit.jupiter.api.Test;
//
//import java.awt.image.BufferedImage;
//
//public class Tickets {
//
//
//    @Test
//    public void test() {
//        try {
//            FXMLLoader fxmlLoader =ViewFactory.getTicketV1();
//            Pane vd = fxmlLoader.load();
//
////            TicketController ticketController = fxmlLoader.getController();
////            ticketController.setTicketDetails(qrTicket);
//            BufferedImage bufferedImage = ImageUtils.nodeToImage(vd);
//            String folderPath = NewFolder.createTodayFolder();
//            ImageUtils.saveBufferedImage(bufferedImage, folderPath + "\\" + 123 + ".png");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//}
