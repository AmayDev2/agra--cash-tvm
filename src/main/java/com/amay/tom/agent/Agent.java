package com.amay.tom.agent;

import com.amay.tom.api.service.impl.ApiService;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.config.Versions;
import com.amay.tom.enums.DeviceStatus;
import com.amay.tom.enums.TomInitializerListener;
import com.amay.tom.grpc.ccugrpc.CCUTGService;
import com.amay.tom.grpc.monotoring.GrpcApiListener;
import com.amay.tom.grpc.scugrpc.ScuService;
import com.amay.tom.model.bussiness.BusinessList;
import com.amay.tom.model.equipment.dto.EquipmentPrivilegeDto;
import com.amay.tom.model.equipment.entity.EquipmentPrivilege;
import com.amay.tom.model.session.Shift;
import com.amay.tom.model.user.entity.UserPrivilege;
import com.amay.tom.repository.adjustment.AdjustedTicketRepository;
import com.amay.tom.repository.refund.RefundTicketRepository;
import com.amay.tom.repository.sql.SqlGlobalRepository;
import com.amay.tom.repository.sqlite.SqliteRepositoryImpl;
import com.amay.tom.repository.tickets.TicketsRepository;
import com.amay.tom.service.base36.ShiftIdGeneratorService;
import com.amay.tom.service.devices.ImpDeviceStatusListener;
import com.amay.tom.service.devices.PeripheralMonitor;
import com.amay.tom.service.initialize.impl.TomInitialize;
import com.amay.tom.service.siftservice.InternalListener;
import com.amay.tom.service.siftservice.ShiftService;
import com.amay.tom.service.tom.IApplicationService;
import com.amay.tom.service.userauth.UserAuth;
import com.amay.tom.threadpool.ThreadPool;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;

import java.sql.Connection;

@Data
public class Agent {

    private SystemConfig systemConfig;
    private EquipmentPrivilegeDto equipmentPrivilegeDto;
    private UserPrivilege userPrivilege;
    private EquipmentPrivilege equipmentPrivilege;
    private ScuService scuService;
    private ScuService ccuService;
    private SqliteRepositoryImpl sqliteRepository;
    private SqlGlobalRepository sqlGlobalRepository;
    private TicketsRepository ticketsRepository;
    private ThreadPool threadPool;
    private Connection  Connection;
    private UserAuth userAuth;
    private ShiftService shiftService;
    private IApplicationService applicationService;
    private GrpcApiListener grpcApiListener;
    private GrpcApiListener ccuGrpcApiListener;
    private PeripheralMonitor peripheralMonitor;
    private InternalListener internalListener;  //shift Listener
    private TomInitialize tomInitialize;
    private AdjustedTicketRepository adjustedTicketRepository;
    private Shift shift;
    private CCUTGService ccutgService;
    private ImpDeviceStatusListener deviceStatusListener;
    private DeviceStatus deviceStatus;
    private final StringProperty operationMode = new SimpleStringProperty();
    private TomInitializerListener tomInitializerListener;
    private ApiService apiService;
    private RefundTicketRepository refundTicketRepository;
    private ShiftIdGeneratorService shiftIdGeneratorService;
    private BusinessList businessRule;
    private Versions versions;




//    public Agent(SystemConfig systemConfig, EquipmentPrivilegeDto equipmentPrivilegeDto) {
//        this.systemConfig = systemConfig;
//        this.equipmentPrivilegeDto = equipmentPrivilegeDto;
//    }


}
