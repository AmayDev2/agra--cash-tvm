package com.amay.tom.service.siftservice;

import com.amay.tom.enums.EOSType;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.util.Optional;

public interface ShiftService {
public FXMLLoader startShift(String username, String password) throws Exception;
public void endOfShift(EOSType eosType);
public void pauseShift();
public void resumeShift(String password);
void setMainStage(Stage mainStage);
Optional<String> checkLastShiftCompletion();
void markLastShiftAsCompleted(String shift);
public void printEOShift(String shiftId);
void printEOSReport(String shiftId);
}
