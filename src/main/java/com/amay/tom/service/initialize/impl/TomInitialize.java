package com.amay.tom.service.initialize.impl;

import com.amay.printer.PrinterCommandDispatcher;
import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.api.UserMapper;
import com.amay.tom.config.ENVURL;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.config.TicketConfig;
import com.amay.tom.config.Versions;
import com.amay.tom.config.dto.TicketConfigDTO;
import com.amay.tom.controller.LoginController;
import com.amay.tom.controller.TomInitializeViewController;
import com.amay.tom.database.SQLConnector;
import com.amay.tom.database.SQLiteConnector;
import com.amay.tom.enums.DeviceOperationMode;
import com.amay.tom.enums.DeviceStatus;
import com.amay.tom.enums.TomInitializerListener;
import com.amay.tom.grpc.ccugrpc.CCUGrpcConnector;
import com.amay.tom.grpc.ccugrpc.CCUTGService;
import com.amay.tom.grpc.monotoring.*;
import com.amay.tom.grpc.scugrpc.SCUGrpcConnector;
import com.amay.tom.grpc.scugrpc.ScuService;
import com.amay.tom.listener.ModesListener;
import com.amay.tom.model.Station;
import com.amay.tom.model.bussiness.BusinessList;
import com.amay.tom.model.ccuRest.Users;
import com.amay.tom.model.equipment.EquipmentMapper;
import com.amay.tom.model.equipment.dto.EquipmentPrivilegeDto;
import com.amay.tom.model.faretable.FareMatrixDTO;
import com.amay.tom.model.user.dto.UserDto;
import com.amay.tom.model.user.dto.UserPrivilegeDto;
import com.amay.tom.repository.FareLine3;
import com.amay.tom.repository.StationData;
import com.amay.tom.repository.adjustment.AdjustedTicketRepository;
import com.amay.tom.repository.adjustment.AdjustedTicketRepositoryImpl;
import com.amay.tom.repository.sql.SqlGlobalRepository;
import com.amay.tom.repository.sql.SqlRepositoryImpl;
import com.amay.tom.repository.sqlite.SqliteRepositoryImpl;
import com.amay.tom.repository.tickets.TicketsRepository;
import com.amay.tom.repository.tickets.TicketsRepositoryImpl;
import com.amay.tom.repository.user.UserRepository;
import com.amay.tom.repository.user.UserRepositoryImpl;
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
import com.amay.tom.threadpool.ThreadPool;
import com.amay.tom.utils.StationData1;
import com.amay.tom.utils.env.EnvFile;
import com.amay.tom.utils.env.EnvLoader;
import com.amay.tom.utils.helper.Helper;
import com.amay.tom.utils.jsonFile.JsonFileWriterUtil;
import com.amay.tvm.bnr.BNRIntegration;
import com.amay.tvm.coin.CoinModuleInterface;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Any;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.amaytechnosystems.*;
import org.json.JSONObject;
import org.network.monitorandcontrol.tom.TOMDeviceInfo;
//import org.network.monitorandcontrol.tr.TRProtocol;
import org.tinylog.Logger;
import org.transaction.qr.QrTransactionGrpc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;


public class TomInitialize implements ITomInitialize {

//    private final String version;
    private final TomInitializeViewController tomInitializeViewController;
    private final Agent agent;
    private final IApplicationService applicationService;
    private final int steps = 20;
    private final TomInitializerListener tomInitializerListener;
    private IApi apiConnection = null;
    private ScuService scuService;
    private ScuService ccuService;
    private EquipmentPrivilegeDto equipmentPrivilegeDto;
    private GrpcApiListener grpcApiListener,ccuGrpcApiListener;
    private EnvLoader envLoader = null;
    private int progress = 0;
    private boolean isVersionCompatible;
    private ImpDeviceStatusListener deviceStatusListener;

    {
//        try {
////            MavenXpp3Reader reader = new MavenXpp3Reader();
////            Model model = reader.read(new FileReader("pom.xml"));
////            String version = model.getVersion();
////            this.version = version;
////            System.out.println("Project version: " + version);
//        } catch (XmlPullParserException | IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    public TomInitialize(TomInitializeViewController tomInitializeViewController, IApi apiConnection, IApplicationService applicationService) {
        this.applicationService = applicationService;
        this.tomInitializeViewController = tomInitializeViewController;
        this.apiConnection = apiConnection;
//        this.scuService = new ScuService();
        this.setEquipmentPrivilege();
        this.envLoader = new EnvLoader(ENVURL.CONFIG+"\\tvm-config.env");
        this.apiConnection.setIpPort(this.envLoader.getCcuIpAddressActual(),this.envLoader.getRestPort());
        this.agent = new Agent();
        this.tomInitializerListener = new TomInitializerListener((val) -> {
            try {
                this.onSuccessfulInitialization(val);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        this.agent.setTomInitializerListener(this.tomInitializerListener);

    }

    @Override
    public boolean loadEnv() {
        EnvFile.loadEnv();
        return true;
    }

    public void getSQLightDBConnection() throws RuntimeException, InterruptedException {
        this.updateUI(progress, "Connection with sqlite local DB...");
        String dbUrl = envLoader.getSQLiteDatabasePath();
        String dbFileName = envLoader.getSQLiteDatabaseName();
        int noOfConnections = envLoader.getSQLiteDatabaseConnections();
        SQLiteConnector sqLiteConnector = new SQLiteConnector(dbUrl, dbFileName, noOfConnections);
        sqLiteConnector.setSQLiteConnection();

        Connection connection = sqLiteConnector.getConnection();
        SqliteRepositoryImpl sqliteRepository = new SqliteRepositoryImpl(connection);
        System.out.println("Connection established");
        agent.setSqliteRepository(sqliteRepository);

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
        System.out.println("Connection established");
        agent.setSqlGlobalRepository(sqlRepository);
        this.updateUI(++progress, "Connection with sql local DB stabled...");
    }

    // SCU Monitoring Service
    private void monitoringService() {
        this.updateUI(progress, "Setting up monitoring service...");
        String ccuIp = envLoader.getCcuIpAddress();
        int ccuPort = envLoader.getCcuPort();
        CCUMonitoringConnector ccuMonitoringConnector = new CCUMonitoringConnector(ccuIp, ccuPort,false);
        org.network.monitorandcontrol.MonitorAndControlGrpc.MonitorAndControlStub monitorAndControlStub = ccuMonitoringConnector.getAsyncStub();
        this.updateUI(progress, "sep1 done...");
        GrpcControlMonitoringService grpcControlMonitoringService = new GrpcControlMonitoringService(monitorAndControlStub, applicationService, ccuMonitoringConnector, agent.getThreadPool(),"SCU",new DataPushService(new SCUPushService(this.agent)));
        grpcApiListener = new GrpcApiListener(grpcControlMonitoringService, agent);
        agent.setGrpcApiListener(grpcApiListener);
        this.applicationService.addListener(grpcApiListener);
        this.updateUI(progress, "sep2 done...");
        grpcControlMonitoringService.initialConnectionRequest(RequestHandler.getInitialRequest());
        this.updateUI(++progress, "Monitoring service set up...");
    }

    // New Method to setup CCU monitoring connection
    private void CcuMonitoringService() {
        this.updateUI(progress, "Setting up  ccu monitoring service...");
        String ccuIp = envLoader.getCcuIpAddressActual();
        int ccuPort = envLoader.getCcuPortActual();
        System.out.println("CCU IP "+ccuIp + " " + ccuPort);
        CCUMonitoringConnector ccuMonitoringConnector = new CCUMonitoringConnector(ccuIp, ccuPort,true);
        org.network.monitorandcontrol.MonitorAndControlGrpc.MonitorAndControlStub monitorAndControlStub = ccuMonitoringConnector.getAsyncStub();
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
    public void setCCUConnection() throws IOException {
        this.monitoringService();
    }

    @Override
    public void setCcuMonitoringService() throws IOException {
        this.CcuMonitoringService();
    }

    @Override
    public void setSCUConnection() throws IOException {
        this.updateUI(progress, "Setting up SCU connection...");
        String scuIp = envLoader.getCcuIpAddress();
        int scuPort = envLoader.getCcuPort();
        SCUGrpcConnector scuGrpcConnector = new SCUGrpcConnector(scuIp, scuPort);
        TomTransactionServiceGrpc.TomTransactionServiceBlockingStub scuStub = scuGrpcConnector.getBlockingStub();
        this.updateUI(progress, "sep 1 done...");
        ScuService scuService = new ScuService(scuGrpcConnector, scuStub,"SCU");
        this.updateUI(progress, "sep 2 done...");
        this.agent.setScuService(scuService);
        this.scuService = scuService;
        this.updateUI(++progress, "SCU connection set up...");
    }

    // New Method to setup CCU connection to send Transactions data
    @Override
    public void setCCUConnectionActual() throws IOException {
        this.updateUI(progress, "Setting up CCU connection...");
        String ccuIp = envLoader.getCcuIpAddressActual();
        int ccuPort = envLoader.getCcuPortActual();
        SCUGrpcConnector ccuGrpcConnector = new SCUGrpcConnector(ccuIp, ccuPort);
        TomTransactionServiceGrpc.TomTransactionServiceBlockingStub ccuStub = ccuGrpcConnector.getBlockingStub();
        this.updateUI(progress, "sep 1 done...");
        ScuService ccuService = new ScuService(ccuGrpcConnector, ccuStub,"CCU");
        this.updateUI(progress, "sep 2 done...");
        this.agent.setCcuService(ccuService);
        this.ccuService = ccuService;
        this.updateUI(++progress, "CCU connection set up...");

    }

    @Override
    public void setCCUTGConnection() throws IOException {

        this.updateUI(progress, "Setting up SCU connection...");
        String ccuIp = envLoader.getCcuIpAddress();
        int ccuPort = envLoader.getCcuPort();
        CCUGrpcConnector ccuGrpcConnector = new CCUGrpcConnector(ccuIp, 9000);
        QrTransactionGrpc.QrTransactionBlockingStub scuStub = ccuGrpcConnector.getBlockingStub();
        this.updateUI(progress, "sep 1 done...");
        CCUTGService ccuService = new CCUTGService(scuStub);
        this.updateUI(progress, "sep 2 done...");
        this.agent.setCcutgService(ccuService);
        this.updateUI(++progress, "SCU connection set up...");

    }

    @Override
    public boolean versionCheck() {
        return false;
//        String deviceSerial = envLoader.getEquipmentSerialNew();
//        String deviceType = "TOM";
//        String deviceId = envLoader.getEquipmentIdNew();
////        this.updateUI(progress, "Checking version..." + this.version);
//        try {
//            if (this.scuService.getVersion(ScuDataMapper.createVersionRequest(deviceType, deviceId, deviceSerial)).equalsIgnoreCase(this.version)) {
//                System.out.println("Version matched");
//                this.updateUI(++progress, "Version checked...");
//                return true;
//            }
//            System.out.println("Version mismatched");
//            this.updateUI(++progress, "Version checked...");
//            return false;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }

    }


    //Access the equipment privilege from the JSON file
    private void setEquipmentPrivilege() {
        try {
            // Get the file path from the environment variable
            String filePath = EnvFile.getEquipmentPrivileges();
            File file = new File(filePath);

            // Check if the file exists
            if (file.exists()) {
                // Create an ObjectMapper instance
                ObjectMapper objectMapper = new ObjectMapper();

                // Configure ObjectMapper to use reflection access
                objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
                objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

                // Convert JSON file content to EquipmentPrivilegeDto object
                EquipmentPrivilegeDto equipmentPrivilegeDto = objectMapper.readValue(file, EquipmentPrivilegeDto.class);

                // If the object is not null, set it to the instance variable
                if (equipmentPrivilegeDto != null) {
                    this.equipmentPrivilegeDto = equipmentPrivilegeDto;
                }
            }
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
    public void deviceInitialization() {
        new Thread(() -> {
            try {
                this.updateUI(0.2, "Checking device authorization...");
                Thread.currentThread().setName("TomInitialize");
                this.initializeThreadPool();
                this.getVersion();

                this.getSQLightDBConnection();
//                Thread.sleep(5000);
                this.getSQLDBConnection();
                this.setupRepositories();

                this.updateUI(progress, "My Software Version : "+this.agent.getVersions().getVersion());
                Thread.sleep(5000);
                this.updateSoftware();

                this.loadStations();
                this.loadTicketConfig();
                this.getFareTable();
                this.checkStationMode();
                this.getUserDataTableVersion();
                this.updateUI(0.4, "Connecting to server ...");


                this.setCCUConnectionActual();
                this.setSCUConnection();
//                Thread.sleep(5000);
                this.setCCUConnection(); // SCU MONITORING
                this.setCcuMonitoringService(); //CCU  MONITORING
//                Thread.sleep(5000);

                // Thread.sleep(5000);
                //this.setCCUTGConnection();

                this.updateUI(0.6, "Pushing status on server...");

                //isVersionCompatible = this.versionCheck();
//                this.getUserDataTableVersion();
                // Thread.sleep(5000);
//                this.initializeThreadPool();
                // Thread.sleep(5000);
//                this.getFareTableUpdate();


                // Thread.sleep(5000);
                this.peripheralDeviceStatus();
                this.updateBusinessTime();
                //this.pushRemainedDate();
//                BNRIntegration.bnrOpen();
//                CoinModuleInterface.INSTANCE.setupCoinModule(envLoader.getComPort());
                PrinterCommandDispatcher.INSTANCE.setupPrinter();
                this.onSuccessfulInitialization(null);
                this.updateUI(0.9, "All done...");
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }, "TomInitialize").start();

    }

    private void getVersion() {
        Package pkg = com.amay.tom.Main.class.getPackage();
        String title    = pkg.getImplementationTitle();
        String version  = pkg.getImplementationVersion();
        String vendor   = pkg.getImplementationVendor();
        this.agent.setVersions(
                new Versions(
                        version
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
                launcherPath,                                    // Path to launcher.bat
                dir,
                oldFilename,
                Integer.toString(delaySec),
                newFileDir,
                newFileName,
                pid ,                                            // Current app PID
                runCommand

        );

        System.out.println("Launching detached updater: " + command);

        try {
            new ProcessBuilder(command)
                    .inheritIO()
                    .start();                                   // Don't wait—detached
            System.out.println("Updater launched successfully. Exiting application...");
            // Exit immediately so the batch can run independently
            new Remote(new AppCloseCommand(applicationService)).pressButton();
        } catch (IOException e) {
            System.err.println("Failed to start launcher: " + e.getMessage());
            e.printStackTrace();
        }
    }




    private void updateSoftware()  {
        try {
            this.updateUI(progress, "Checking for software updates...");
            if (!this.envLoader.getIsUpdate()) return;
            String host = this.envLoader.getFTPHost();//"localhost";
            int port = this.envLoader.getFTPPort();//2222;
            String username = this.envLoader.getFTPUsername();//"sftpuser";
            String password = this.envLoader.getFTPPassword();//"sftp123";
            String remoteFilePath = this.envLoader.getFTPRemoteFilePath();//"test/Tom.jar"; // Path on the SFTP server
            String localFilePath = this.envLoader.getFTPLocalPath() + "/" + this.envLoader.getLocalFileName();// "Tom_New.jar"; // Local path to save the file
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
            System.out.println("Current PID = " + pid);

            callLauncher(launcher, dir, oldFilename, delay, this.envLoader.getFTPLocalPath(), this.envLoader.getLocalFileName(), runCommand, String.valueOf(pid), applicationService);
        } catch (Exception e) {
            this.updateUI(progress, "Error in updating software: " + e.getMessage());
        }


    }

    private void updateBusinessTime() {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                BusinessList businessList = objectMapper.readValue(new File(EnvFile.getCalendar()), BusinessList.class);
                this.updateUI(progress, "Loading stations...");
//                String profileResponse = this.apiConnection.getCalender();
//                String profileResponse1 = this.apiConnection.getBusinessDay();
//                String profileResponse2 = this.apiConnection.getpeakTime();
//                if (null == profileResponse) return;
//                CalenderConfig[] calenderConfigs = Helper.JSONtoObjectAR(profileResponse, CalenderConfig[].class);
//                PeakTimeConfig[] peakTimeConfigs = Helper.JSONtoObjectAR(profileResponse2, PeakTimeConfig[].class);
//                BusinessDayConfig[] businessDayConfigs = Helper.JSONtoObjectAR(profileResponse1, BusinessDayConfig[].class);
//
//                businessList = new BusinessList().setBusinessDays(Arrays.stream(businessDayConfigs).toList())
//                        .setPeakTimes(Arrays.stream(peakTimeConfigs).toList())
//                        .setSpecialDayCalender(Arrays.stream(calenderConfigs).toList());
//                JsonFileWriterUtil.writeToJsonFile(businessList, EnvFile.getCalendar());
                this.agent.setBusinessRule(businessList);
                this.agent.getBusinessRule().today();
                System.out.println("Business Rule : "+this.agent.getBusinessRule().toString());
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


    }


    private void loadTicketConfig() {
try {
    TicketConfig.INSTANT.getTicketConfig();
    System.out.println("Ticket  Config  "+TicketConfig.INSTANT.getProductTypeDefDTO().toString());
    String profileResponse = this.apiConnection.getTicketConfig();
    TicketConfigDTO configData = (TicketConfigDTO) Helper.JSONtoObject(profileResponse, TicketConfigDTO.class);
    if (configData == null) {
        System.out.println("TicketConfigDTO is null");
        return;
    }

    JsonFileWriterUtil.writeToJsonFile(configData, EnvFile.getTicketConfigFile());
} catch (RuntimeException e) {
    e.printStackTrace();
}
    }

    @Override
    public void checkStationMode() {
        //check station mode
//        new Remote(new InServiceCardCommand(applicationService));
        DeviceStatus deviceStatus = new DeviceStatus(DeviceOperationMode.IN_SERVICE);
        agent.setDeviceStatus(deviceStatus);
    }



    @Override
    public void loadStations() {
        try {
            this.updateUI(progress, "Loading stations...");
            StationData.getInstance();
            String profileResponse = this.apiConnection.getStations();
            if (null == profileResponse) return;
            StationData1[] stationsData = Helper.JSONtoObjectAR(profileResponse, StationData1[].class);
            List<Station> stations = new ArrayList<>();
            if (stationsData == null || stationsData.length == 0) {
                System.out.println("No stations data found");
                this.updateUI(++progress, "No stations data found");
                return;
            }

            for (StationData1 station : stationsData) {
                stations.add(new Station(
                        station.getStationId()
                        , station.getStationName()));
            }
            JsonFileWriterUtil.writeToJsonFile(stations, EnvFile.getStationsFile());
            StationData.getInstance();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void pushRemainedDate() {
//        Logger.debug("SCU CONNECTED: " + agent.getPeripheralMonitor().isScu_connected());
//        Logger.debug("CCU CONNECTED: " + agent.getPeripheralMonitor().isCcu_connected());
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
        return true;
    }

    @Override
    public boolean getFareTable() {


        this.updateUI(progress, "Loading Faretable...");
        FareLine3.retrieveData("distanceMatrix.ser");
        String faretable = this.apiConnection.getFareTable();
        if (faretable == null || faretable.isEmpty()) {
            System.out.println("Fare table response is empty or null");
            return false;
        }
        FareMatrixDTO fareMatrixDTO = (FareMatrixDTO) Helper.JSONtoObject(faretable, FareMatrixDTO.class);
        JSONObject json = new JSONObject(faretable);
        if (fareMatrixDTO == null  ) {
            System.out.println("FareMatrixDTO is null");
            return false;
        }
        int [][] distanceMatrix = new int[fareMatrixDTO.matrix().size()][fareMatrixDTO.matrix().size()];
        int row = -1;
        System.out.println("Fare Matrix Size: " + fareMatrixDTO.matrix().size());

        for(var x: fareMatrixDTO.stationId()){
            ++row;
            int col = -1;
            for(var y: fareMatrixDTO.stationId()){
                distanceMatrix[row][++col] = fareMatrixDTO.matrix().get(x).get(y);
                System.out.print(fareMatrixDTO.matrix().get(x).get(y)+" ");
            }
            System.out.println();
        }

        FareLine3.saveData("distanceMatrix.ser",distanceMatrix);
        FareLine3.retrieveData("distanceMatrix.ser");
        return false;
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
    public boolean getUserDataTableVersion() {
        this.updateUI(progress, "Loading users...");
        try {
//            Object ob = this.apiConnection.getUsersTableVersion();
            this.getUserUpdatedTable();

        } catch (RuntimeException e) {
            Logger.info("Not able to fetch data of User");
        }
        return false;
    }

    @Override
    public boolean getUserUpdatedTable() {
//        String profileResponse = this.apiConnection.getUserProfile();
//        UserProfile[] profiles = Helper.JSONtoObjectAR(profileResponse, UserProfile[].class);
//
//        String usersResponse = this.apiConnection.getAllUsers();
//        Users[] users = Helper.JSONtoObjectAR(usersResponse, Users[].class);

        String userWithProfileResponse = this.apiConnection.getAllUserWithProfile();
        Users[] userWithProfile = Helper.JSONtoObjectAR(userWithProfileResponse, Users[].class);

        System.out.println("loaded User  "+ Arrays.toString(Arrays.stream(userWithProfile).toArray()));

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
        System.out.println("Mapped " + userDtoList.size() + " users.");
        System.out.println("Mapped " + userPrivilegeList.size() + " privilege sets.");

        try {
            UserRepository userRepository=  new UserRepositoryImpl(
                    agent.getConnection());
            for(UserDto user: userDtoList)
                userRepository.save(user);

            for(UserPrivilegeDto userPrivilegeDto: userPrivilegeList)
                userRepository.saveUserPrivilege(userPrivilegeDto);

        }catch (RuntimeException | SQLException ex){
            ex.printStackTrace();
        }

        return false;
    }


    @Override
    public boolean peripheralDeviceStatus() {
        PeripheralMonitor peripheralMonitor = new PeripheralMonitor(this.ccuGrpcApiListener, this.grpcApiListener);
        agent.setPeripheralMonitor(peripheralMonitor);
        deviceStatusListener = new ImpDeviceStatusListener(ccuGrpcApiListener);
        peripheralMonitor.addDeviceStatusListener(deviceStatusListener);
        deviceStatusListener = new ImpDeviceStatusListener(grpcApiListener);
        peripheralMonitor.addDeviceStatusListener(deviceStatusListener);
        agent.setDeviceStatusListener(deviceStatusListener);

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
