package com.amay.tom.service.siftservice;

import com.amay.tom.enums.EOSType;
import com.amay.tom.model.session.Shift;
import com.amay.tom.pdu.controller.service.SceneManager;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.util.Optional;

public interface ShiftService {
public FXMLLoader startShift(String username, String password) throws Exception;


 FXMLLoader startMaintenanceShift(String username, String password, SceneManager sceneManager) throws Exception;

 public void endOfShift(EOSType eosType);
 public void pauseShift();
 public void resumeShift(String password);
 void setMainStage(Stage mainStage);
 Optional<String> checkLastShiftCompletion();
 void markLastShiftAsCompleted(String shift);
 void printShiftReport(String shiftId);
 void printFinanceReports(String shiftId);
// public void printEOShift(String shiftId);
 void printEOSReport(String shiftId);
 void updateCashInventory(String shiftId);
}
