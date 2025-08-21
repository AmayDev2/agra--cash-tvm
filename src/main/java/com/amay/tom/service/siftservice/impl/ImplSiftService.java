package com.amay.tom.service.siftservice.impl;

import com.amay.tom.enums.DeviceOperationMode;
import com.amay.tom.grpc.monotoring.GrpcApiListener;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.controller.Controller;
import com.amay.tom.controllerInterface.controllerInt.ControllerAdapter;
import com.amay.tom.database.DatabaseConnector;
import com.amay.tom.enums.DeviceStatus;
import com.amay.tom.enums.EOSType;
import com.amay.tom.enums.FareMedium;
import com.amay.tom.model.siftdata.ShiftDetail;
import com.amay.tom.model.siftdata.ShiftHeader;
import com.amay.tom.model.siftdata.User;
import com.amay.tom.repository.QRDataArray;
import com.amay.tom.repository.ShiftDetailsRepo;
import com.amay.tom.repository.TicketRecordRepository;
import com.amay.tom.service.siftservice.SiftService;
import com.amay.tom.utils.time.TimeUtil;
import org.network.monitorandcontrol.OperationMode;
import org.tinylog.Logger;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicInteger;

public enum ImplSiftService implements SiftService {

    INSTANCE;
    private GrpcApiListener grpcApiListener;
    {
        this.grpcApiListener = new  GrpcApiListener(null, null);
    }

    public void endOfShift(EOSType eosType) {
        AtomicInteger ticketCount = new AtomicInteger();
        AtomicInteger totalSales = new AtomicInteger();
        Connection connection= null;

        try{
            connection= DatabaseConnector.getConnection();
            QRDataArray.createQRTicketFile();
            Logger.info("Stocks Sale {} ; Stock Total{} ", FareMedium.QR.getFareMediumTotal(),FareMedium.QR.getFareMediumTotal());
//            QRDataArray.readQRTicketFile();
//            QRDataArray.createQRTicketFile();
//            ShiftDetail currentShift=ShiftDetailsRepo.getInstance().readShiftDetails(DatabaseConnector.getConnection(),ShiftHeader.getInstance().getShiftId());
            ShiftDetail shiftDetail=switch (eosType){
                case OPERATOR -> ShiftDetailsRepo.getInstance().completeShiftDetails(DatabaseConnector.getConnection(),ShiftHeader.getInstance().getShiftId());
                case SCU -> ShiftDetailsRepo.getInstance().shiftLogout(DatabaseConnector.getConnection(),ShiftHeader.getInstance().getShiftId(),"completed");
                case TIME_OUT -> ShiftDetailsRepo.getInstance().shiftLogout(DatabaseConnector.getConnection(),ShiftHeader.getInstance().getShiftId(),"completed");
                case OTHER -> ShiftDetailsRepo.getInstance().completeShiftDetails(DatabaseConnector.getConnection(),ShiftHeader.getInstance().getShiftId());
                default -> ShiftDetailsRepo.getInstance().completeShiftDetails(DatabaseConnector.getConnection(),ShiftHeader.getInstance().getShiftId());
            };

            Logger.info("updated shift details {}",shiftDetail.toString());

            ShiftHeader.getInstance().setShiftEnd(shiftDetail.getShiftEnd().toString());

            TicketRecordRepository.getInstance().readAllTicketOfCurrentShift(connection,ShiftHeader.getInstance().getShiftId()).forEach(ticketRecord -> {
            Logger.info("Ticket Record: {}",ticketRecord.toString());
            ticketCount.addAndGet(ticketRecord.getCount());
            totalSales.addAndGet(ticketRecord.getCount() * ticketRecord.getPrice());

        });


            Logger.info("---About to logout---");
            //logout  , show login screen
            if(eosType==EOSType.SCU)ControllerAdapter.INSTANCE.setLogout();

        }catch (Exception e){
            Logger.error("Error in reading ticket records {}",e.getMessage());
        }
        Logger.info("Shift ended. Total tickets sold: {} Total sales: {}",ticketCount.get(),totalSales.get());
    }

    @Override
    public void pauseShift() {
        Logger.debug("Command Shift paused");
        this.grpcApiListener.sendOperationMode(OperationMode.OUT_OF_SERVICE);
        try{
            Logger.info("Shift paused with id {}",ShiftHeader.getInstance().getShiftId());
            SystemConfig.getInstance().setDeviceCurrentStatus(DeviceOperationMode.PAUSE);
            ShiftDetail shiftDetail=ShiftDetailsRepo.getInstance().updateShiftStatus(DatabaseConnector.getConnection(),ShiftHeader.getInstance().getShiftId(),"paused");
            ShiftDetailsRepo.getInstance().updateShiftDetails(DatabaseConnector.getConnection(),shiftDetail);
            Controller.getController().PauseShift();
        }catch (Exception e){
            Logger.error("Error in pausing shift {}",e.getMessage());
        }
    }

    public void resumeShift() {
        try{
            Logger.info("Shift resumed with id {}",ShiftHeader.getInstance().getShiftId());
            SystemConfig.getInstance().setDeviceCurrentStatus(DeviceOperationMode.IN_SERVICE);
            ShiftDetail shiftDetail=ShiftDetailsRepo.getInstance().updateShiftStatus(DatabaseConnector.getConnection(),ShiftHeader.getInstance().getShiftId(),"active");
//            ShiftDetailsRepo.getInstance().updateShiftDetails(DatabaseConnector.getConnection(),shiftDetail);
            Controller.getController().ResumeShift();
        }catch (Exception e){
            Logger.error("Error in resuming shift {}",e.getMessage());
        }
    }



    public void startShift(User user) {
        ShiftHeader.getInstance().setInstance(SystemConfig.getInstance().getCurrentStation().getStationName(),SystemConfig.getInstance().getCurrentEquipment().getEquipmentSerial(),user.getUserId(), ShiftHeader.getInstance().getShiftId(), String.valueOf(new Date(System.currentTimeMillis())));
        ShiftDetail shiftDetail=new ShiftDetail();
        shiftDetail.setShiftStart(new Timestamp(TimeUtil.getCurrentTimeInLong()));
        shiftDetail.setShiftEnd(new Timestamp(TimeUtil.getCurrentTimeInLong()));
        shiftDetail.setStationId(SystemConfig.getInstance().getCurrentStation().getStationId());
        shiftDetail.setEquipmentId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId());
        shiftDetail.setOperatorId(user.getUserId());
        shiftDetail.setShiftStatus("active");
        try{
        int id=ShiftDetailsRepo.getInstance().createShiftDetails(DatabaseConnector.getConnection(),shiftDetail);
            FareMedium.QR.setFareMediumTotal(0);
            FareMedium.NCMC.setFareMediumTotal(0);

        Logger.info("Shift started with id {}",id);
            ShiftHeader.getInstance().setShiftId(id);
        }catch (Exception e){
            Logger.error("Error in creating shift details {}",e.getMessage());
        }
    }

    public boolean forceShutdownCheck(){
        try{
            ShiftDetail shiftDetail= ShiftDetailsRepo.getInstance().getLastShiftDetails(DatabaseConnector.getConnection());
            if(null!=shiftDetail &&  shiftDetail.getShiftStatus().equals("active")){
//                ShiftHeader.getInstance().setShiftId(shiftDetail.getShiftId());
                ShiftHeader.getInstance().setInstance(SystemConfig.getInstance().getCurrentStation().getStationName(),SystemConfig.getInstance().getCurrentEquipment().getEquipmentSerial(),shiftDetail.getOperatorId(),shiftDetail.getShiftId(),shiftDetail.getShiftStart().toString());

                Logger.info("Force shutdown detected. Shift id {}",shiftDetail.getShiftId());
                System.out.println(shiftDetail.toString());
                return true;

            }
            return false;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}

