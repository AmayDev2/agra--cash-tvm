package com.amay.tom.service.initialize.impl;

import com.amay.printer.PrinterCommandDispatcher;
import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.api.UserMapper;
import com.amay.tom.config.ENVURL;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.config.TicketConfig;
import com.amay.tom.config.Versions;
import com.amay.tom.config.dto.*;
import com.amay.tom.controller.LoginController;
import com.amay.tom.controller.TomInitializeViewController;
import com.amay.tom.database.SQLConnector;
import com.amay.tom.enums.DeviceOperationMode;
import com.amay.tom.enums.DeviceStatus;
import com.amay.tom.enums.TomInitializerListener;
import com.amay.tom.grpc.monotoring.*;
import com.amay.tom.grpc.scugrpc.SCUGrpcConnector;
import com.amay.tom.grpc.scugrpc.ScuDataMapper;
import com.amay.tom.grpc.scugrpc.ScuService;
import com.amay.tom.listener.ModesListener;
import com.amay.tom.model.Equipment;
import com.amay.tom.model.TicketType;
import com.amay.tom.model.business.*;
import com.amay.tom.model.ccuRest.Users;
import com.amay.tom.model.equipment.EquipmentMapper;
import com.amay.tom.model.equipment.dto.EquipmentDto;
import com.amay.tom.model.equipment.dto.EquipmentPrivilegeDto;
import com.amay.tom.model.faretable.FareMatrixDTO;
import com.amay.tom.model.faretable.FareRowEntity;
import com.amay.tom.model.product.Product;
import com.amay.tom.model.product.ProductDTO;
import com.amay.tom.model.product.ProductEntity;
import com.amay.tom.model.product.ProductMapper;
import com.amay.tom.model.station.Station;
import com.amay.tom.model.station.StationEntity;
import com.amay.tom.model.tomConfig.TomConfigDto;
import com.amay.tom.model.tomConfig.TomConfigMapper;
import com.amay.tom.model.tvmConfig.TvmConfigMapper;
import com.amay.tom.model.user.dto.UserDto;
import com.amay.tom.model.user.dto.UserPrivilegeDto;
import com.amay.tom.model.version.MasterConfigInfoMapper;
import com.amay.tom.pdu.controller.command.HeaderCommand;
import com.amay.tom.pdu.controller.command.PDUCommandDispatcher;
import com.amay.tom.repository.FareLine3;
import com.amay.tom.repository.Replacement.ReplacementTicketRepository;
import com.amay.tom.repository.Replacement.ReplacementTicketRepositoryImpl;
import com.amay.tom.repository.StationData;
import com.amay.tom.repository.adjustment.AdjustedTicketRepository;
import com.amay.tom.repository.adjustment.AdjustedTicketRepositoryImpl;
import com.amay.tom.repository.business.*;
import com.amay.tom.repository.equipment.EquipmentRepository;
import com.amay.tom.repository.equipment.EquipmentRepositoryImpl;
import com.amay.tom.repository.fareTable.FareTableRepository;
import com.amay.tom.repository.fareTable.FareTableRepositoryImpl;
import com.amay.tom.repository.product.ProductRepository;
import com.amay.tom.repository.product.ProductRepositoryImpl;
import com.amay.tom.repository.refund.RefundTicketRepository;
import com.amay.tom.repository.refund.RefundTicketRepositoryImpl;
import com.amay.tom.repository.session.ShiftRepository;
import com.amay.tom.repository.session.ShiftRepositoryImpl;
import com.amay.tom.repository.sql.SqlGlobalRepository;
import com.amay.tom.repository.sql.SqlRepositoryImpl;
import com.amay.tom.repository.station.StationRepository;
import com.amay.tom.repository.station.StationRepositoryImpl;
import com.amay.tom.repository.tickets.TicketsRepository;
import com.amay.tom.repository.tickets.TicketsRepositoryImpl;
import com.amay.tom.repository.tomConfig.TomConfigRepository;
import com.amay.tom.repository.tomConfig.TomConfigRepositoryImpl;
import com.amay.tom.repository.tvmConfig.TvmConfigRepository;
import com.amay.tom.repository.tvmConfig.TvmConfigRepositoryImpl;
import com.amay.tom.repository.user.UserRepository;
import com.amay.tom.repository.user.UserRepositoryImpl;
import com.amay.tom.repository.version.VersionRepository;
import com.amay.tom.repository.version.VersionRepositoryImpl;
import com.amay.tom.service.api.IApi;
import com.amay.tom.service.devices.ImpDeviceStatusListener;
import com.amay.tom.service.devices.PeripheralMonitor;
import com.amay.tom.service.events.Remote;
import com.amay.tom.service.initialize.ITomInitialize;
import com.amay.tom.service.qrService2.DataPushService;
import com.amay.tom.service.qrService2.push.CCUPushService;
import com.amay.tom.service.qrService2.push.SCUPushService;
import com.amay.tom.service.tom.IApplicationService;
import com.amay.tom.service.update.SFTPDownloader;
import com.amay.tom.service.versions.VersionService;
import com.amay.tom.test.MonitoringConnector;
import com.amay.tom.test.MonitoringService;
import com.amay.tom.threadpool.ThreadPool;
import com.amay.tom.utils.StationData1;
import com.amay.tom.utils.env.EnvFile;
import com.amay.tom.utils.env.EnvLoader;
import com.amay.tom.utils.equipments.EquipmentUtil;
import com.amay.tom.utils.helper.Helper;
import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.backend.repository.*;
import com.amay.tvm.bnr.BNRIntegration;
import com.amay.tvm.coin.CoinModuleInterface;
import com.amay.tom.model.tvmConfig.TvmConfigDto;
import com.amay.tvm.ups.UPS;
import com.amay.tvm.ups.exception.UPSCommunicationException;
import com.amay.tvm.ups.model.UPSResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Any;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import lombok.val;
import org.amaytechnosystems.TomTransactionServiceGrpc.TomTransactionServiceBlockingStub;
import org.json.JSONObject;
import org.network.monitorandcontrol.MonitorAndControlGrpc.MonitorAndControlStub;
import org.network.monitorandcontrol.tom.TOMEquipmentInfo;
import org.network.monitorandcontrol.tr.TRProtocol;
import org.tinylog.Logger;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;



public class TomInitialize implements ITomInitialize {

//    private final String version;
    private final TomInitializeViewController tomInitializeViewController;
    private final Agent agent;
    private final IApplicationService applicationService;
    private final int steps = 20;
    private IApi apiConnection = null;
    private ScuService scuService;
    private EquipmentPrivilegeDto equipmentPrivilegeDto;
    private GrpcApiListener grpcApiListener,ccuGrpcApiListener;
    private EnvLoader envLoader = null;
    private int progress = 0;
    private boolean isVersionCompatible;


    public TomInitialize(TomInitializeViewController tomInitializeViewController, IApi apiConnection, IApplicationService applicationService) {
        this.applicationService = applicationService;
        this.tomInitializeViewController = tomInitializeViewController;
        this.apiConnection = apiConnection;
//        this.scuService = new ScuService();
        this.setEquipmentPrivilege();
        this.envLoader = new EnvLoader(ENVURL.CONFIG+".env");
        this.apiConnection.setIpPort(this.envLoader.getCcuIpAddress(),this.envLoader.getRestPort());
        this.agent = new Agent();
        TomInitializerListener tomInitializerListener = new TomInitializerListener((val) -> {
            try {
                this.onSuccessfulInitialization(val);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        this.agent.setTomInitializerListener(tomInitializerListener);

    }

    @Override
    public boolean loadEnv() {
        EnvFile.loadEnv();
        EnvLoader envLoader = new EnvLoader(ENVURL.CONFIG+".env");
        String currentStationId = envLoader.getStationIdNew();
        StationData.getInstance();
        String currentStationName = StationData.getInstance().getStation(currentStationId).getStationName();
        String currentEquipmentId = envLoader.getEquipmentIdNew();
        String currentEquipmentSerial = envLoader.getEquipmentSerialNew();
        String lineNumber = envLoader.getLineNumber();
        SystemConfig.getInstance(currentStationId, currentStationName, currentEquipmentId, currentEquipmentSerial,lineNumber);
        PDUCommandDispatcher.INSTANCE.dispatch(new HeaderCommand(SystemConfig.getInstance().getCurrentStation()));
        return true;
    }

    public void getSQLightDBConnection() throws RuntimeException, InterruptedException {
        this.updateUI(progress, "Connection with sqlite local DB...");
        String dbUrl = envLoader.getSQLiteDatabasePath();
        String dbFileName = envLoader.getSQLiteDatabaseName();
        int noOfConnections = envLoader.getSQLiteDatabaseConnections();
//        SQLiteConnector sqLiteConnector = new SQLiteConnector(dbUrl, dbFileName, noOfConnections);
//        sqLiteConnector.setSQLiteConnection();

//        Connection connection = sqLiteConnector.getConnection();
//        SqliteRepositoryImpl sqliteRepository = new SqliteRepositoryImpl(connection);
        //System.out.println("Connection established");
//        agent.setSqliteRepository(sqliteRepository);

        this.updateUI(++progress, "Connection with sqlite local DB stabled...");
    }

    @Override
    public void getSQLDBConnection() throws RuntimeException, InterruptedException {

        this.updateUI(progress, "Connection with sql local DB...");
        String dbUrl = envLoader.getDatabaseUrl();
        String dbUsername = envLoader.getDatabaseUsername();
        String dbPassword = envLoader.getDatabasePassword2();
        int noOfConnections = envLoader.getSQLiteDatabaseConnections();
        SQLConnector sqlConnector = new SQLConnector(dbUrl, dbUsername, dbPassword, noOfConnections);
        sqlConnector.setConnection();
        Connection connection = sqlConnector.getConnection();
        agent.setConnection(connection);
        SqlGlobalRepository sqlRepository = new SqlRepositoryImpl(connection);
        //System.out.println("Connection established");
        agent.setSqlGlobalRepository(sqlRepository);
        this.updateUI(++progress, "Connection with sql local DB stabled...");
    }

    @Override
    public void setSCUMonitoringService() throws IOException {
        this.updateUI(progress, "Setting up scu monitoring service...");
        String ccuIp = envLoader.getScuIpAddress();
        int ccuPort = envLoader.getScuPort();
        CCUMonitoringConnector ccuMonitoringConnector = new CCUMonitoringConnector(ccuIp, ccuPort,false);
        MonitorAndControlStub monitorAndControlStub = ccuMonitoringConnector.getAsyncStub();
        this.updateUI(progress, "sep1 done...");
        GrpcControlMonitoringService grpcControlMonitoringService = new GrpcControlMonitoringService(monitorAndControlStub, applicationService, ccuMonitoringConnector, agent.getThreadPool(),"SCU",new DataPushService(new SCUPushService(this.agent)));
        grpcApiListener = new GrpcApiListener(grpcControlMonitoringService, agent);
        agent.setGrpcApiListener(grpcApiListener);
        this.applicationService.addListener(grpcApiListener);
        this.updateUI(progress, "sep2 done...");
        grpcControlMonitoringService.initialConnectionRequest(RequestHandler.getInitialRequest());
        this.updateUI(++progress, "Monitoring service set up...");
    }

    @Override
    public void setCCUMonitoringService() throws IOException {
        this.updateUI(progress, "Setting up  ccu monitoring service...");
        String ccuIp = envLoader.getCcuIpAddress();
        int ccuPort = envLoader.getCcuPort();
        //System.out.println("CCU IP "+ccuIp + " " + ccuPort);
        CCUMonitoringConnector ccuMonitoringConnector = new CCUMonitoringConnector(ccuIp, ccuPort,true);
        MonitorAndControlStub monitorAndControlStub = ccuMonitoringConnector.getAsyncStub();
        this.updateUI(progress, "sep1 done...");
        GrpcControlMonitoringService grpcControlMonitoringService = new GrpcControlMonitoringService(monitorAndControlStub, applicationService, ccuMonitoringConnector, agent.getThreadPool(),"CCU", new DataPushService(new CCUPushService(this.agent)));
        this.ccuGrpcApiListener = new GrpcApiListener(grpcControlMonitoringService, agent);
        agent.setCcuGrpcApiListener(this.ccuGrpcApiListener);
        this.applicationService.addListener(this.ccuGrpcApiListener);
        this.updateUI(progress, "sep2 done...");
        grpcControlMonitoringService.initialConnectionRequest(RequestHandler.getInitialRequest());
        this.updateUI(++progress, "Monitoring service set up...");
    }

    @Override
    public void setSCUTransactionConnection() throws IOException {
        this.updateUI(progress, "Setting up SCU connection...");
        String scuIp = envLoader.getScuIpAddress();
        int scuPort = envLoader.getScuPort();
        SCUGrpcConnector scuGrpcConnector = new SCUGrpcConnector(scuIp, scuPort);
        TomTransactionServiceBlockingStub scuStub = scuGrpcConnector.getBlockingStub();
        this.updateUI(progress, "sep 1 done...");
        ScuService scuService = new ScuService(scuGrpcConnector, scuStub,"SCU");
        this.updateUI(progress, "sep 2 done...");
        this.agent.setScuService(scuService);
        this.scuService = scuService;
        this.updateUI(++progress, "SCU connection set up...");
    }

    // New Method to setup CCU connection to send Transactions data
    @Override
    public void setCCUTransactionConnection() throws IOException {
        this.updateUI(progress, "Setting up CCU connection...");
        String ccuIp = envLoader.getCcuIpAddress();
        int ccuPort = envLoader.getCcuPort();
        SCUGrpcConnector ccuGrpcConnector = new SCUGrpcConnector(ccuIp, ccuPort);
        TomTransactionServiceBlockingStub ccuStub = ccuGrpcConnector.getBlockingStub();
        this.updateUI(progress, "sep 1 done...");
        ScuService ccuService = new ScuService(ccuGrpcConnector, ccuStub,"CCU");
        this.updateUI(progress, "sep 2 done...");
        this.agent.setCcuService(ccuService);
        this.updateUI(++progress, "CCU connection set up...");

    }



    //Access the equipment privilege from the JSON file
    private void setEquipmentPrivilege() {
        try {
//            // Get the file path from the environment variable
//            String filePath = EnvFile.getEquipmentPrivileges();
//            File file = new File(filePath);
//
//            // Check if the file exists
//            if (file.exists()) {
//                // Create an ObjectMapper instance
//                ObjectMapper objectMapper = new ObjectMapper();
//
//                // Configure ObjectMapper to use reflection access
//                objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
//                objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
//
//                // Convert JSON file content to EquipmentPrivilegeDto object
//                EquipmentPrivilegeDto equipmentPrivilegeDto = objectMapper.readValue(file, EquipmentPrivilegeDto.class);
//
//                // If the object is not null, set it to the instance variable
//                if (equipmentPrivilegeDto != null) {
//                    this.equipmentPrivilegeDto = equipmentPrivilegeDto;
//                }
//            }
            this.equipmentPrivilegeDto=new EquipmentPrivilegeDto();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUI(double progress, String info) {
        tomInitializeViewController.updateProgress(progress, info);
    }

    public void onSuccessfulInitialization(Scene val) throws IOException {
        agent.setTomInitialize(this);
        FXMLLoader fxmlLoader = ViewFactory.getLogin();
        agent.setSystemConfig(SystemConfig.getInstance());
        agent.setEquipmentPrivilegeDto(this.equipmentPrivilegeDto);
        agent.setEquipmentPrivilege(EquipmentMapper.mapToEquipmentPrivilege(this.equipmentPrivilegeDto));
        this.applicationService.addListener(new ModesListener(agent));
        agent.setScuService(scuService);
        agent.setApplicationService(this.applicationService);
        fxmlLoader.setControllerFactory(param -> new LoginController(agent));
        tomInitializeViewController.onSuccessfulInitialization(val, fxmlLoader);
    }

    private void onFailedInitialization() {
        updateUI(0.0, "Initialization failed");
    }

    @Override
    public boolean deviceAuthentication() {

        return false;
    }

    @Override
    public boolean deviceAuthorization() {
        return false;
    }

    @Override
    public boolean deviceConfiguration() {
        return false;
    }

    /**
     * @return
     */
    @Override
    public boolean isClockSynchronized() {
        return false;
    }

    /**
     *
     */
    @Override
    public void synchronizeClock() {

    }

    /**
     *
     */
    @Override
    public void initializeThreadPool() {
        //create all type of thread pools
        agent.setThreadPool(new ThreadPool(10));
    }

    @Override
    public void deviceInitialization(Scene scene) {
        new Thread(() -> {
            try {
                Thread.currentThread().setName("TomInitialize");
                double progress = 0.0;

                // 1. Initialize Thread Pool
                this.updateUI(progress, "Initializing thread pool...");
                this.initializeThreadPool();
                progress += 0.04;
                this.updateUI(progress, "Thread pool initialized.");

                getEquipment();

                // 2. Get Version
                this.getVersion();
                progress += 0.04;
                this.updateUI(progress, "Version fetched.");

                // 3. SQLight DB
                this.getSQLightDBConnection();
                progress += 0.04;
                this.updateUI(progress, "SQL DB connected.");

                // 4. SQL DB
                this.getSQLDBConnection();
                progress += 0.04;
                this.updateUI(progress, "SQL DB connected.");


                // 5. Setup Repositories
                this.setupRepositories();
                progress += 0.04;
                this.updateUI(progress, "Repositories setup done.");

                // 6. Version Check
                VersionService versionService = this.getMasterVersion();
                Logger.debug("Version Service expected : " + versionService);
                progress += 0.03;
                this.updateUI(progress, "Software Version : " + this.agent.getVersions().getVersion());
                Thread.sleep(1000);

                // 7. Update Software
                if(this.updateSoftware((versionService.getExpected()!=null) && (!versionService.getMasterConfigInfoCheck().isTvmSwVer()))) {
                    versionService.getActual().setTvmSwVer(versionService.getExpected().getTvmSwVer());
                }
                progress += 0.05;
                this.updateUI(progress, "Software updateToAdd done.");

                // 8. Load Stations
                if(this.loadStations((versionService.getExpected()!=null) && (!versionService.getMasterConfigInfoCheck().isTopologyConfig()))) {
                    versionService.getActual().setTopologyConfig(versionService.getExpected().getTopologyConfig());
                }
                progress += 0.05;
                this.updateUI(progress, "Stations loaded.");

                // 9. Load Ticket Config
                if(this.loadTicketConfig((versionService.getExpected()!=null) && (!versionService.getMasterConfigInfoCheck().isTicketConfig()))) {
                    versionService.getActual().setTicketConfig(versionService.getExpected().getTicketConfig());
                }
                progress += 0.04;
                this.updateUI(progress, "Ticket config loaded.");

                // 10. Load Product Config
                if(this.loadProduct((versionService.getExpected()!=null) && (!versionService.getMasterConfigInfoCheck().isProductConfig()))) {
                    versionService.getActual().setProductConfig(versionService.getExpected().getProductConfig());
                }
                progress += 0.04;
                this.updateUI(progress, "Product config loaded.");

                // 11. Load Fare Table
                if(this.getFareTable((versionService.getExpected()!=null) && (!versionService.getMasterConfigInfoCheck().isFareConfig()))) {
                    versionService.getActual().setFareConfig(versionService.getExpected().getFareConfig());
                }
                progress += 0.04;
                this.updateUI(progress, "Fare table loaded.");

                // 12. Load User Data
                if(this.getUserDataTableVersion(!versionService.getMasterConfigInfoCheck().isUserVer() || !versionService.getMasterConfigInfoCheck().isProfileVer())) {
                    versionService.getActual().setUserVer(versionService.getExpected().getUserVer());
                    versionService.getActual().setProfileVer(versionService.getExpected().getProfileVer());
                }
                progress += 0.04;
                this.updateUI(progress, "User data loaded.");


                if(this.loadTomConfig((versionService.getExpected()!=null) && (!versionService.getMasterConfigInfoCheck().isTomConfig()))) {
                    versionService.getActual().setTomConfig(versionService.getExpected().getTomConfig());
                }
                if(this.loadTvmConfig((versionService.getExpected()!=null) && (!versionService.getMasterConfigInfoCheck().isTvmConfig()))) {
                    versionService.getActual().setTvmConfig(versionService.getExpected().getTvmConfig());
                }

                // 13. Update Business Time
                if(this.updateBusinessTime((versionService.getExpected()!=null)
                        && (!versionService.getMasterConfigInfoCheck().isBusinessDayVer()
                        || !versionService.getMasterConfigInfoCheck().isPeakTimeVer()
                        || !versionService.getMasterConfigInfoCheck().isCalenderConfig()))) {
                    versionService.getActual().setBusinessDayVer(versionService.getExpected().getBusinessDayVer());
                    versionService.getActual().setPeakTimeVer(versionService.getExpected().getPeakTimeVer());
                    versionService.getActual().setCalenderConfig(versionService.getExpected().getCalenderConfig());
                }
                progress += 0.05;
                this.updateUI(progress, "Business time updated.");

                // 14. Save Version to Repository
                if((versionService.getExpected()!=null) && (versionService.getActual()!=null)) {
                    agent.getVersionRepository().deleteAll();
                    versionService.getActual().setConfigVer(versionService.getExpected().getConfigVer());
                    agent.getVersionRepository().insert(MasterConfigInfoMapper.dtoToEntity(versionService.getActual()));
                }
                if(agent.getVersionRepository().findAll().size()>0)
                    agent.setMasterConfigInfo(MasterConfigInfoMapper.entityToMasterConfigInfo(agent.getVersionRepository().findAll().getFirst()));
                //TODO: Load tvm config
               if(agent.getTomConfigRepository().findAll().size()>0)
                    agent.setTomConfig(TomConfigMapper.entityToModel(agent.getTomConfigRepository().findAll().getFirst()));
                ScuDataMapper.setVersion(agent.getMasterConfigInfo());
                progress += 0.04;
                this.updateUI(progress, "Version info saved.");

                // 15. Load Environment
                this.loadEnv();
                progress += 0.03;
                this.updateUI(progress, "Environment loaded.");

                // 20. Check Station Mode
                this.checkStationMode();
                progress += 0.03;
                this.updateUI(progress, "Station mode checked.");




                PrinterCommandDispatcher.INSTANCE.setupPrinter();
                UPS.INTERFACE.setupUPS(envLoader.getUPS_COM_PORT());
                // 21. Peripheral Status
                if(envLoader.getEnvironment()) {
                    try {
                        progress += 0.03;
                        this.updateUI(progress, "Connecting BNR,COIN MODULE & PRINTER.");
                        CoinModuleInterface.INSTANCE.setupCoinModule(envLoader.getComPort(),agent);
                        BNRIntegration.bnrOpen();
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }

                // 16. CCU Transaction
                this.setCCUTransactionConnection();
                progress += 0.03;
                this.updateUI(progress, "CCU transaction connection set.");

                // 17. CCU Monitoring
                this.setCCUMonitoringService();
                progress += 0.03;
                this.updateUI(progress, "CCU monitoring service set.");


                // 18. SCU Transaction
                this.setSCUTransactionConnection();
                progress += 0.03;
                this.updateUI(progress, "SCU transaction connection set.");

                // 19. SCU Monitoring
                this.setSCUMonitoringService();
                progress += 0.03;
                this.updateUI(progress, "SCU monitoring service set.");

                this.peripheralDeviceStatus();
                agent.getThreadPool().getScheduler().scheduleAtFixedRate(agent.getPeripheralMonitor(),0,5,TimeUnit.SECONDS);
                progress += 0.03;
                this.updateUI(progress, "Peripheral status pushed.");

                Thread.sleep(6000);

                // 22. Push Remaining Data & Finalize
//                this.pushRemainedDate();
                this.onSuccessfulInitialization(scene);
                progress = 1.0;
                this.updateUI(progress, "Device initialization complete.");

            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }, "TomInitialize").start();
    }


    private boolean loadTomConfig(boolean isUpdate) {
        this.updateUI(progress, "Loading tomConfig...");
        try {
            this.updateUI(progress, "Loading tomConfig...");

            if (isUpdate) {
                String productResponse = this.apiConnection.getTomConfig();
                if (productResponse == null) {
                    this.updateUI(progress, "Product data not found.");
                    Logger.debug("Product data not found.");
                    throw new RuntimeException("Product data not found.");
                }
                TomConfigDto tomConfigDto = Helper.JSONtoObjectAR(productResponse, TomConfigDto.class);
                if(tomConfigDto!=null) {
                    agent.getTomConfigRepository().deleteAll();
                    agent.getTomConfigRepository().insert(TomConfigMapper.dtoToEntity(tomConfigDto));
                    Logger.debug("TomConfig loaded: " + tomConfigDto.toString());
                }
            }
        } catch (Exception e) {
            Logger.debug("Error in loading TomConfig: " + e.getMessage());
            this.updateUI(progress, "Error in loading TomConfig: " + e.getMessage());
            isUpdate = false;
        }
        this.updateUI(++progress, "TomConfig loaded successfully.");
        return isUpdate;
    }

    private boolean loadTvmConfig(boolean isUpdate) {
        this.updateUI(progress, "Loading tvmConfig...");
        try {
            this.updateUI(progress, "Loading tvmConfig...");
//            isUpdate=true;

            if (isUpdate) {
                String productResponse = this.apiConnection.getTvmConfig();
                if (productResponse == null) {
                    this.updateUI(progress, "Product data not found.");
                    Logger.debug("Product data not found.");
                    throw new RuntimeException("Product data not found.");
                }
//                TomConfigDto tomConfigDto = Helper.JSONtoObjectAR(productResponse, TomConfigDto.class);
                TvmConfigDto tvmConfigDto = Helper.JSONtoObjectAR(productResponse, TvmConfigDto.class);
                if(tvmConfigDto!=null) {
                    agent.getTvmConfigRepository().deleteAll();
                    agent.getTvmConfigRepository().insert(TvmConfigMapper.dtoToEntity(tvmConfigDto));
                    Logger.debug("TomConfig loaded: " + tvmConfigDto.toString());
                }
            }
        } catch (Exception e) {
            Logger.debug("Error in loading TomConfig: " + e.getMessage());
            this.updateUI(progress, "Error in loading TomConfig: " + e.getMessage());
            isUpdate = false;
        }
        this.updateUI(++progress, "TomConfig loaded successfully.");
        return isUpdate;
    }

    private void getEquipment() {
        String response = this.apiConnection.getEquipmentDetails();
        if (response == null)
            return;
        try {
            EquipmentDto equipmentDto = new ObjectMapper().readValue(response, EquipmentDto.class);
            com.amay.tom.model.equipment.entity.Equipment equipment = EquipmentMapper.convertEquipmentDtoToEntity(equipmentDto);
            EquipmentUtil.setEquipment(equipment);
        } catch (JsonProcessingException e) {
            Logger.error("Error parsing JSON response from CCU for Equipment details", e);
        } catch (RuntimeException e) {
            Logger.error("Error while getting Equipment data: " + e.getMessage(), e);
        }
    }


    public void loadEquipmentDetailsFromDb() {
        EquipmentRepository equipmentRepository = agent.getEquipmentRepository();
        com.amay.tom.model.equipment.entity.Equipment equipmentDetails = equipmentRepository.get();
        if (equipmentDetails != null) {
            String currentStationId = equipmentDetails.getStationId();
            StationData.getInstance();
            String currentStationName = StationData.getInstance().getStation(currentStationId).getStationName();
            String currentEquipmentId = equipmentDetails.getEquipmentId();
            String currentEquipmentSerial = equipmentDetails.getEquipmentSerial();
            String lineNumber = equipmentDetails.getLineId();
            SystemConfig.getInstance(currentStationId, currentStationName, currentEquipmentId, currentEquipmentSerial, lineNumber);
            PDUCommandDispatcher.INSTANCE.dispatch(new HeaderCommand(SystemConfig.getInstance().getCurrentStation()));
        }
    }


    private void initializeEquipmentDetails(){
        EquipmentRepository equipmentRepository = agent.getEquipmentRepository();
        com.amay.tom.model.equipment.entity.Equipment equipmentFromDb = equipmentRepository.get();
        com.amay.tom.model.equipment.entity.Equipment equipmentFromCCU = EquipmentUtil.getEquipment();
        if(equipmentFromDb==null && equipmentFromCCU!=null){
            equipmentRepository.insertIfEmpty(equipmentFromCCU);
        }
    }


    private boolean loadProduct(boolean isUpdate) {
        this.updateUI(progress, "Loading product...");
        try{
            this.updateUI(progress, "Loading product...");

            if(isUpdate) {
                String productResponse = this.apiConnection.getProduct();
                if (productResponse == null) {
                    this.updateUI(progress, "Product data not found.");
                    Logger.debug("Product data not found.");
                    throw new RuntimeException("Product data not found.");
                }
                ProductDTO[] productDTOS = Helper.JSONtoObjectAR(productResponse, ProductDTO[].class);
//                Products products= new Products().setProductList(productDTOS);
                //Map to entity
//                JsonFileWriterUtil.writeToJsonFile(products, EnvFile.getProductDefinitionFile());

                //Store in db
                agent.getProductRepository().deleteAll();
                for(ProductDTO product : productDTOS) {
                    agent.getProductRepository().insert(ProductMapper.dtoToEntity(product));
                }
                //get from db
                Logger.debug("Product loaded: " + TicketConfig.INSTANT.getProductTypeDefDTO().toString());
            }

//            Products savdProducts=(Products)JsonFileWriterUtil.readFileToJsonObject(EnvFile.getProductDefinitionFile(), Products.class);
            List<ProductEntity> savedProductEntities = agent.getProductRepository().findAll();
            List<Product> savedProducts = new ArrayList<>();
            for (ProductEntity productEntity : savedProductEntities){
                savedProducts.add(ProductMapper.EntityToProduct(productEntity));
            }
            //TICKET TYPE SJT
            //TICKET TYPE RJT
            //TICKET TYPE GJT
            for(Product product : savedProducts) {
                if (product.isActive() && product.getProductid().equals(TicketType.SINGLE.getTicketTypeId())) {
                    TicketType.SINGLE.setProduct(product);
                }else if (product.isActive() && product.getProductid().equals(TicketType.RETURN.getTicketTypeId())) {
                        TicketType.RETURN.setProduct(product);
                    }
                else if (product.isActive() && product.getProductid().equals(TicketType.GROUP.getTicketTypeId())) {
                        TicketType.GROUP.setProduct(product);
                    }
                else if (product.isActive() && product.getProductid().equals(TicketType.PAID.getTicketTypeId())) {
                        TicketType.PAID.setProduct(product);
                    }
                else if (product.isActive() && product.getProductid().equals(TicketType.FREE.getTicketTypeId())) {
                        TicketType.FREE.setProduct(product);
                    } else if (product.isActive() && product.getProductid().equals(TicketType.MQR_SINGLE.getTicketTypeId())) {
                TicketType.MQR_SINGLE.setProduct(product);
            } else if (product.isActive() && product.getProductid().equals(TicketType.MQR_RETURN.getTicketTypeId())) {
                TicketType.MQR_RETURN.setProduct(product);
            } else if (product.isActive() && product.getProductid().equals(TicketType.MQR_GROUP.getTicketTypeId())) {
                TicketType.MQR_GROUP.setProduct(product);
            }
//                else if (product.isActive() && product.getProductid().equals(TicketType.TEST.getTicketTypeId())) {
//                        TicketType.TEST.setProduct(product);
//                    }
            }

            Logger.debug("Saved Products: " + savedProducts);
            this.agent.setProducts(savedProducts);


        }catch (Exception e){
            Logger.debug("Error in loading product: " + e.getMessage());
            this.updateUI(progress, "Error in loading product: " + e.getMessage());
            isUpdate=false;
        }
        this.updateUI(++progress, "Product loaded successfully.");
        return isUpdate;
    }



    private   VersionService getMasterVersion() {
        MasterConfigInfoDTO masterConfigInfoDTO =null, myMasterConfigInfoDTO = null;
        this.updateUI(progress, "Getting master version...");
        try {
            String masterVersion = this.apiConnection.getMasterVersion();
//            //System.out.println("masterVersion : "+masterVersion);
            if (masterVersion == null || masterVersion.isEmpty()) {
                throw new RuntimeException("Master Config version not found.");
            }

            masterConfigInfoDTO = (MasterConfigInfoDTO) Helper.JSONtoObject(masterVersion, MasterConfigInfoDTO.class);
            if (masterConfigInfoDTO == null) {
                throw new RuntimeException("Master Config version Could not be parsed.");
            }
            Logger.debug("Master Version: " + masterVersion);
            this.updateUI(++progress, "Master version fetched successfully.");

//            myMasterConfigInfoDTO =(MasterConfigInfoDTO) JsonFileWriterUtil.readFileToJsonObject( EnvFile.getMasterConfigFile(), MasterConfigInfoDTO.class);



        }catch (Exception e) {
            Logger.error(e.getMessage());
            this.updateUI(progress, e.getMessage());
        }
        try{
            myMasterConfigInfoDTO = MasterConfigInfoMapper.entityToDto(agent.getVersionRepository().findAll().getFirst());
        }catch (Exception e){
            Logger.error(e.getMessage());
        }
        if(null== myMasterConfigInfoDTO) myMasterConfigInfoDTO = new MasterConfigInfoDTO();
        myMasterConfigInfoDTO.setTvmSwVer(this.agent.getVersions().getVersion());


        // call Version service to get boolean
        VersionService versionService=new VersionService();
//        Logger.debug("Expected version config "+masterConfigInfoDTO);
        if(masterConfigInfoDTO==null){
            myMasterConfigInfoDTO=new MasterConfigInfoDTO();
        }
        versionService.compareVersion(masterConfigInfoDTO, myMasterConfigInfoDTO);
        return versionService;

    }

    private void getVersion() {
        Package pkg = com.amay.tom.Main.class.getPackage();
        String title    = pkg.getImplementationTitle();
        String version  = pkg.getImplementationVersion();
        String vendor   = pkg.getImplementationVendor();
        this.agent.setVersions(
                new Versions(
                        version==null ? "1.1.7" : version
                )
        );
    }

    BiConsumer<Double, String> updateProgress = this::updateUI;
    public static void callLauncher(
            String launcherPath,
            String dir,
            String oldFilename,
            int delaySec,
            String newFileDir,
            String newFileName,
            String runCommand,
            String pid,
            IApplicationService applicationService
    ) {
        List<String> command = Arrays.asList(
                "cmd.exe", "/C",               // Use detached start
                "start", "/B","",               // Start in detached mode
                launcherPath,                    // Path to launcher.bat
                dir,
                oldFilename,
                Integer.toString(delaySec),
                newFileDir,
                newFileName,
                pid ,                                            // Current app PID
                runCommand

        );

        //System.out.println("Launching detached updater: " + command);

        try {
            new ProcessBuilder(command)
                    .inheritIO()
                    .start();                                   // Don't wait—detached
            //System.out.println("Updater launched successfully. Exiting application...");
            // Exit immediately so the batch can run independently
            new Remote(new AppCloseCommand(applicationService)).pressButton();
        } catch (IOException e) {
            System.err.println("Failed to start launcher: " + e.getMessage());
            e.printStackTrace();
        }
    }




    private boolean updateSoftware( boolean isUpdate)  {
        this.updateUI(progress, "Checking for software updates...");
        try {
//            isUpdate=true;
            if(isUpdate) {
                String host = this.envLoader.getFTPHost();//"localhost";
                int port = this.envLoader.getFTPPort();//2222;
                String username = this.envLoader.getFTPUsername();//"sftpuser";
                String password = this.envLoader.getFTPPassword();//"sftp123";
                String remoteFilePath = this.envLoader.getFTPRemoteFilePath();//"test/Tom.jar"; // Path on the SFTP server
                String localFilePath = this.envLoader.getFTPLocalPath() + "\\" +  this.envLoader.getLocalZipName();// "Tom_New.jar"; // Local path to save the file
                SFTPDownloader.downloadFile(host, port, username, password, remoteFilePath, localFilePath, updateProgress);

                String launcher = this.envLoader.getApplicationLauncherPath();
                String dir = this.envLoader.getApplicationPath();//"C:\\tvm-config";
                String oldFilename = this.envLoader.getCurrentFileName(); //"Tom.jar";
                int delay = this.envLoader.getLaunchDelay(); //2;
                String fxLib = this.envLoader.getFXMLLib();//"C:\\Program Files\\javafx-sdk-21.0.7\\lib";
                String modules = "javafx.controls,javafx.fxml";
                String newFile = this.envLoader.getFTPLocalPath() + "\\" + this.envLoader.getLocalFileName();
                String runCommand = String.format(
                        "java --module-path %s --add-modules %s -jar %s",
                        fxLib, modules, newFile
                );

                String vmName = ManagementFactory.getRuntimeMXBean().getName(); // e.g. "12345@hostname"
                long pid = Long.parseLong(vmName.split("@")[0]);
                //System.out.println("Current PID = " + pid);

                callLauncher(launcher, dir, oldFilename, delay, this.envLoader.getFTPLocalPath(), this.envLoader.getLocalFileName(), runCommand, String.valueOf(pid), applicationService);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            Logger.error("Error in updating software: " + e.getMessage());
            this.updateUI(progress, "Error in updating software: " + e.getMessage());
            isUpdate=false;
        }
        return isUpdate;


    }

    private boolean updateBusinessTime(boolean isUpdate) {
        BusinessDayConfigRepository businessDayConfigRepository = agent.getBusinessDayConfigRepository();
        CalendarConfigRepository calendarConfigRepository = agent.getCalendarConfigRepository();
        PeakTimeConfigRepository peakTimeConfigRepository = agent.getPeakTimeConfigRepository();
            try {
                this.updateUI(progress, "Loading Business...");
//                BusinessList businessList = (BusinessList) JsonFileWriterUtil.readFileToJsonObject(EnvFile.getCalendar(),BusinessList.class);
                List<BusinessDayConfigDTO> businessDayConfigDTOS = new ArrayList<>();
                List<CalendarConfigDTO> calendarConfigDTOS = new ArrayList<>();
                List<PeakTimeConfigDTO> peakTimeConfigDTOS = new ArrayList<>();
                for(BusinessDayConfigEntity businessDayConfigEntity : businessDayConfigRepository.findAll()){
                    businessDayConfigDTOS.add(BusinessDayConfigMapper.entityToDto(businessDayConfigEntity));
                }
                for(CalendarConfigEntity calendarConfigEntity : calendarConfigRepository.findAll()){
                    calendarConfigDTOS.add(CalendarConfigMapper.entityToDto(calendarConfigEntity));
                }
                for(PeakTimeConfigEntity peakTimeConfigEntity : peakTimeConfigRepository.findAll()){
                    peakTimeConfigDTOS.add(PeakTimeConfigMapper.entityToDto(peakTimeConfigEntity));
                }
                BusinessList businessList = new BusinessList().setBusinessDays(businessDayConfigDTOS)
                        .setPeakTimes(peakTimeConfigDTOS)
                        .setSpecialDayCalender(calendarConfigDTOS);
                this.agent.setBusinessRule(businessList);

                if(isUpdate) {
                    this.updateUI(++progress, "Business time updating...");
                    String profileResponse = this.apiConnection.getCalender();
                    String profileResponse1 = this.apiConnection.getBusinessDay();
                    String profileResponse2 = this.apiConnection.getPeakTime();
                    if (null == profileResponse || profileResponse.isBlank()) profileResponse = "[]";
                    if (null == profileResponse1 || profileResponse1.isBlank()) profileResponse1 = "[]";
                    if (null == profileResponse2 || profileResponse2.isBlank()) profileResponse2 = "[]";
                    CalendarConfigDTO[] calenderConfigs = Helper.JSONtoObjectAR(profileResponse, CalendarConfigDTO[].class);
                    PeakTimeConfigDTO[] peakTimeConfigs = Helper.JSONtoObjectAR(profileResponse2, PeakTimeConfigDTO[].class);
                    BusinessDayConfigDTO[] businessDayConfigs = Helper.JSONtoObjectAR(profileResponse1, BusinessDayConfigDTO[].class);



                    businessList = new BusinessList().setBusinessDays(Arrays.stream(businessDayConfigs).toList())
                            .setPeakTimes(Arrays.stream(peakTimeConfigs).toList())
                            .setSpecialDayCalender(Arrays.stream(calenderConfigs).toList());
//                    JsonFileWriterUtil.writeToJsonFile(businessList, EnvFile.getCalendar());
                    calendarConfigRepository.deleteAll();
                    peakTimeConfigRepository.deleteAll();
                    businessDayConfigRepository.deleteAll();
                    for(CalendarConfigDTO calendarConfigDTO : calenderConfigs){
                        calendarConfigRepository.insert(CalendarConfigMapper.dtoToEntity(calendarConfigDTO));
                    }

                    for(PeakTimeConfigDTO peakTimeConfigDTO : peakTimeConfigs){
                        peakTimeConfigRepository.insert(PeakTimeConfigMapper.dtoToEntity(peakTimeConfigDTO));
                    }

                    for(BusinessDayConfigDTO businessDayConfigDTO : businessDayConfigs){
                        businessDayConfigRepository.insert(BusinessDayConfigMapper.dtoToEntity(businessDayConfigDTO));
                    }

                    this.agent.setBusinessRule(businessList);
                }

                Logger.info("Business loaded successfully: " + businessList.toString());
                this.agent.getBusinessRule().today();
                Logger.info("Business Rule : "+this.agent.getBusinessRule().toString());
            } catch (RuntimeException e) {
                Logger.error("Error in loading business time: " + e.getMessage());
                this.updateUI(progress, "Error in loading business time: " + e.getMessage());
                isUpdate=false;
            }
            return isUpdate;


    }

    private void trConnectivityTest() {
        var trMoni=new MonitoringConnector("localhost", 9000);
        var serv=new MonitoringService(trMoni.getAsyncStub(),trMoni);
        TRProtocol msj = TRProtocol.newBuilder().setRequestType(org.network.monitorandcontrol.RequestType.DEVICE_INFO)
                .setRequestData(Any.pack(TOMEquipmentInfo.newBuilder()
                        .setEquipId("01010401")
//                        .setDeviceType(DeviceType.TR.name())
                        .setTomIp("192.168.1.3")
                        .setEquipName("TOM_GNDA_001")
                        .build()))
                .build();

        serv.sendMessage(msj);
    }

    private boolean loadTicketConfig( boolean isUpdate) {
    try {
    TicketConfig.INSTANT.getTicketConfig();
    //System.out.println("Ticket  Config  "+TicketConfig.INSTANT.getProductTypeDefDTO().toString());
    if(isUpdate) {
        String profileResponse = this.apiConnection.getTicketConfig();
        TicketConfigDTO configData = (TicketConfigDTO) Helper.JSONtoObject(profileResponse, TicketConfigDTO.class);
        if (configData == null) {
            //System.out.println("TicketConfigDTO is null");
            this.updateUI(progress, "TicketConfigDTO is null");
            throw new RuntimeException("TicketConfigDTO is null");
        }

        TicketConfig.INSTANT.getTicketConfig();
    }
        } catch (RuntimeException e) {
            Logger.error("Error in loading ticket config: " + e.getMessage());
            this.updateUI(progress, "Error in loading ticket config: " + e.getMessage());
            isUpdate=false;
        }
        this.updateUI(++progress, "Ticket config loaded successfully.");
        return isUpdate;
    }

    @Override
    public void checkStationMode() {
        DeviceStatus deviceStatus = new DeviceStatus(DeviceOperationMode.IN_SERVICE);
        agent.setDeviceStatus(deviceStatus);
    }



    @Override
    public boolean loadStations(boolean isUpdate) {
        try {
            this.updateUI(progress, "Loading stations...");
            StationData.getInstance().getStationsArray(agent.getStationRepository());
            if(isUpdate) {
                String profileResponse = this.apiConnection.getStations();
                if (null == profileResponse) throw new RuntimeException("Stations data not found");
                StationData1[] stationsData = Helper.JSONtoObjectAR(profileResponse, StationData1[].class);
                List<Station> stations = new ArrayList<>();
                if (stationsData == null || stationsData.length == 0) {
                    //System.out.println("No stations data found");
                    this.updateUI(++progress, "No stations data found");
                    throw new RuntimeException("No stations data found");
                }
                agent.getStationRepository().deleteAll();
                for (StationData1 station : stationsData) {
                    StationEntity stationEntity = new StationEntity(station.getStationId(), station.getStationName());
                    agent.getStationRepository().insert(stationEntity);
                }
//                JsonFileWriterUtil.writeToJsonFile(stations, EnvFile.getStationsFile());
                StationData.getInstance().getStationsArray(agent.getStationRepository());
                Logger.info("Stations loaded successfully: " + stations.size());
                this.updateUI(++progress, "Stations loaded successfully: " + stations.size());
            }

        } catch (RuntimeException e) {
            Logger.error("Error in loading stations: " + e.getMessage());
            this.updateUI(progress, "Error in loading stations: " + e.getMessage());
            isUpdate=false;
        }
        return isUpdate;
    }

    @Override
    public void pushRemainedDate() {
        Logger.tag(LoggerTag.APP).debug("SCU CONNECTED: " + agent.getPeripheralMonitor().isScu_connected());
        Logger.tag(LoggerTag.APP).debug("CCU CONNECTED: " + agent.getPeripheralMonitor().isCcu_connected());
        if(agent.getPeripheralMonitor().isScu_connected())
            new DataPushService(new SCUPushService(agent)).pushData();
        if(agent.getPeripheralMonitor().isCcu_connected())
            new DataPushService(new CCUPushService(agent)).pushData();
    }

    private boolean setupRepositories() {
        TicketsRepository ticketsRepository = new TicketsRepositoryImpl(agent.getConnection());
        agent.setTicketsRepository(ticketsRepository);
        AdjustedTicketRepository adjustedTicketRepository = new AdjustedTicketRepositoryImpl(agent.getConnection(), ticketsRepository);
        agent.setAdjustedTicketRepository(adjustedTicketRepository);
        RefundTicketRepository refundTicketRepository = new RefundTicketRepositoryImpl(agent.getConnection(), ticketsRepository);
        agent.setRefundTicketRepository(refundTicketRepository);
        ReplacementTicketRepository replacementTicketRepository = new ReplacementTicketRepositoryImpl(agent.getConnection(), ticketsRepository);
        agent.setReplacementTicketRepository(replacementTicketRepository);
        ProductRepository productRepository = new ProductRepositoryImpl(agent.getConnection());
        agent.setProductRepository(productRepository);
        VersionRepository versionRepository = new VersionRepositoryImpl(agent.getConnection());
        agent.setVersionRepository(versionRepository);
        BusinessDayConfigRepository businessDayConfigRepository = new BusinessDayConfigRepositoryImpl(agent.getConnection());
        agent.setBusinessDayConfigRepository(businessDayConfigRepository);
        CalendarConfigRepository calendarConfigRepository = new CalendarConfigRepositoryImpl(agent.getConnection());
        agent.setCalendarConfigRepository(calendarConfigRepository);
        PeakTimeConfigRepository peakTimeConfigRepository = new PeakTimeConfigRepositoryImpl(agent.getConnection());
        agent.setPeakTimeConfigRepository(peakTimeConfigRepository);
        StationRepository stationRepository = new StationRepositoryImpl(agent.getConnection());
        agent.setStationRepository(stationRepository);
        FareTableRepository fareTableRepository = new FareTableRepositoryImpl(agent.getConnection());
        agent.setFareTableRepository(fareTableRepository);
        TomConfigRepository tomConfigRepository = new TomConfigRepositoryImpl(agent.getConnection());
        agent.setTomConfigRepository(tomConfigRepository);
        TvmConfigRepository tvmConfigRepository = new TvmConfigRepositoryImpl(agent.getConnection());
        agent.setTvmConfigRepository(tvmConfigRepository);
        ShiftRepository shiftRepository = new ShiftRepositoryImpl(agent.getConnection());

        agent.setShiftRepository(shiftRepository);
        TransactionRepository transactionRepository = new TransactionRepositoryImpl(agent.getConnection());
        agent.setTransactionRepository(transactionRepository);
        CoinAmountRepository coinAmountRepository= new CoinAmountRepositoryImpl(agent.getConnection());
        agent.setCoinAmountRepository(coinAmountRepository);
        agent.setNoteAmountRepository(new NoteAmountRepositoryImpl(agent.getConnection()));
        agent.setFinanceOperationRepository(new FinanceOperationRepositoryImpl(agent.getConnection(), agent.getNoteAmountRepository()));
        agent.setEquipmentRepository(new EquipmentRepositoryImpl(agent.getConnection()));
        agent.setAmountSnapShotRepository(new AmountSnapShotRepositoryImpl(agent.getConnection()));
        return true;
    }

    @Override
    public boolean getFareTable(boolean isUpdate) {
        try {
            this.updateUI(progress, "Loading Fare table...");
            FareLine3.retrieveData(agent);
            if (isUpdate) {
                String faretable = this.apiConnection.getFareTable();
                if (faretable == null || faretable.isEmpty()) {
                    //System.out.println("Fare table response is empty or null");
                    return false;
                }
                FareMatrixDTO fareMatrixDTO = (FareMatrixDTO) Helper.JSONtoObject(faretable, FareMatrixDTO.class);
                JSONObject json = new JSONObject(faretable);
                if (fareMatrixDTO == null) {
                    //System.out.println("FareMatrixDTO is null");
                    return false;
                }
                int[][] distanceMatrix = new int[fareMatrixDTO.matrix().size()][fareMatrixDTO.matrix().size()];
                int row = -1;
                //System.out.println("Fare Matrix Size: " + fareMatrixDTO.matrix().size());
                agent.getFareTableRepository().deleteAll();

                for (var x : fareMatrixDTO.stationId()) {
                    ++row;
                    int col = -1;
                    for (var y : fareMatrixDTO.stationId()) {
                        distanceMatrix[row][++col] = fareMatrixDTO.matrix().get(x).get(y);
                        agent.getFareTableRepository().insert(new FareRowEntity(String.valueOf(row),String.valueOf(col),distanceMatrix[row][col]));
                        //System.out.print(fareMatrixDTO.matrix().get(x).get(y) + " ");
                    }
                    //System.out.println();
                }

//                FareLine3.saveData("distanceMatrix.ser", distanceMatrix);
                FareLine3.retrieveData(agent);
            }
        } catch (RuntimeException e) {
            Logger.error("Error in loading fare table: " + e.getMessage());
            this.updateUI(progress, "Error in loading fare table: " + e.getMessage());
            isUpdate =false;
        }
        return isUpdate;
    }

    /**
     * @return
     */
    @Override
    public boolean getFareTableVersion() {
        return false;
    }

    /**
     * @return
     */
    @Override
    public boolean getFareTableUpdate() {
        runCommandAndRestart("E:\\Amay Technosystems\\FTPFileTransfer\\ftpfiletransferapplication.exe", "distanceMatrix", "E:\\Amay Technosystems\\Tom\\tom-config\\faretable");
        return false;
    }

    /**
     * @return
     */
    @Override
    public boolean getCalender() {
        return false;
    }

    /**
     * @return
     */
    @Override
    public boolean getCalenderVersion() {
        return false;
    }

    /**
     * @return
     */
    @Override
    public boolean getCalenderUpdate() {
        return false;
    }

    /**
     * @return
     */
    @Override
    public boolean getApplicationUpdate() {
        return false;
    }

    @Override
    public boolean getBlackListCards() {
        return false;
    }

    @Override
    public boolean getUserDataTableVersion(boolean isUpdate) {
        this.updateUI(progress, "Loading users...");
        try {
            return this.getUserUpdatedTable(isUpdate);
        } catch (RuntimeException e) {
            Logger.info("Not able to fetch data of User");
            return false;
        }
    }

    @Override
    public boolean getUserUpdatedTable(boolean isUpdate) {
        this.updateUI(progress, "Loading user data...");

        // Fetching user profiles and users from the API
//        String profileResponse = this.apiConnection.getUserProfile();
//        UserProfile[] profiles = Helper.JSONtoObjectAR(profileResponse, UserProfile[].class);
//
//        String usersResponse = this.apiConnection.getAllUsers();
//        Users[] users = Helper.JSONtoObjectAR(usersResponse, Users[].class);

        try {
        String userWithProfileResponse = this.apiConnection.getAllUserWithProfile();
        Users[] userWithProfile = Helper.JSONtoObjectAR(userWithProfileResponse, Users[].class);
        //System.out.println("loaded User  "+ Arrays.toString(Arrays.stream(userWithProfile).toArray()));

        List<UserDto> userDtoList = new ArrayList<>();
        List<UserPrivilegeDto> userPrivilegeList = new ArrayList<>();

        for (Users user : userWithProfile) {
//            Long profileId = user.getUserProfile();
//            UserProfile matchedProfile = profileMap.get(profileId);

            UserDto userDto = UserMapper.toUserDto(user);
            UserPrivilegeDto privilegeDto = UserMapper.toUserPrivilegeDto(user.getUserProfile());
            privilegeDto.setUsername(user.getUsername());

            userDtoList.add(userDto);
            userPrivilegeList.add(privilegeDto);
        }

        // Example usage (logging or further processing)
        //System.out.println("Mapped " + userDtoList.size() + " users.");
        //System.out.println("Mapped " + userPrivilegeList.size() + " privilege sets.");


            UserRepository userRepository=  new UserRepositoryImpl(
                    agent.getConnection());
            for(UserDto user: userDtoList)
                userRepository.save(user);

            for(UserPrivilegeDto userPrivilegeDto: userPrivilegeList)
                userRepository.saveUserPrivilege(userPrivilegeDto);

        }catch (RuntimeException | SQLException ex){
            Logger.error("Error in loading user data: " + ex.getMessage());
            this.updateUI(progress, "Error in loading user data: " + ex.getMessage());
            isUpdate=false;
        }

        return isUpdate;
    }


    @Override
    public boolean peripheralDeviceStatus() {
        PeripheralMonitor peripheralMonitor = new PeripheralMonitor(this.ccuGrpcApiListener, this.grpcApiListener);
        agent.setPeripheralMonitor(peripheralMonitor);
        ImpDeviceStatusListener deviceStatusListener = new ImpDeviceStatusListener(ccuGrpcApiListener);
        peripheralMonitor.addDeviceStatusListener(deviceStatusListener);
        deviceStatusListener = new ImpDeviceStatusListener(grpcApiListener);
        peripheralMonitor.addDeviceStatusListener(deviceStatusListener);
        agent.setDeviceStatusListener(deviceStatusListener);
        peripheralMonitor.addDeviceStatusListener(agent.getDeviceStatus());

        // Schedule the peripheral monitor to run every 5 seconds
        agent.getThreadPool().getScheduler().scheduleAtFixedRate(peripheralMonitor, 0, 5, TimeUnit.SECONDS);
        return false;
    }


    public boolean runCommandAndRestart(String... command) {

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.inheritIO();

            // Start the process
            Process process = processBuilder.start();

            // Wait for the process to complete
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        // Restart the Java application
//        restartApplication();
        return true;
    }


}
