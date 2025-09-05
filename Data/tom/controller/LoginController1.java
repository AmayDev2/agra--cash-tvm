package com.amay.tom.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.database.SQLiteConnection;
import com.amay.tom.model.equipment.EquipmentMapper;
import com.amay.tom.model.equipment.entity.EquipmentPrivilege;
import com.amay.tom.model.siftdata.User;
import com.amay.tom.model.user.UserMapper;
import com.amay.tom.model.user.dto.UserPrivilegeDto;
import com.amay.tom.model.user.entity.UserPrivilege;
import com.amay.tom.repository.DBUserRepo;
import com.amay.tom.repository.session.ShiftRepositoryImpl;
import com.amay.tom.repository.user.UserRepositoryImpl;
import com.amay.tom.service.devices.ImpDeviceStatusListener;
import com.amay.tom.service.devices.PeripheralMonitor;
import com.amay.tom.service.siftservice.ShiftService;
import com.amay.tom.service.siftservice.SiftService;
import com.amay.tom.service.siftservice.impl.ImplSiftService;
import com.amay.tom.service.siftservice.impl.ShiftServiceImpl;
import com.amay.tom.service.userauth.UserAuth;
import com.amay.tom.service.userauth.UserDetailsService;
import com.amay.tom.utils.env.EnvFile;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.tinylog.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class LoginController1 {


    @FXML
    private ImageView logo;

    @FXML
    private Text messageLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    private SiftService siftService;
    private int timePeriod;
    private Agent agent;
    private ShiftService shiftService;

    public LoginController1(Agent agent) {
        this.agent = agent;
        this.shiftService=new ShiftServiceImpl(
                agent, new UserAuth(
                new UserDetailsService(
                new UserRepositoryImpl(
                        agent.getConnection()))),new ShiftRepositoryImpl(agent.getConnection()));

    }


    public void setTimePeriod(int timePeriod) {
        this.timePeriod = timePeriod;
    }

    public void initialize() {
        siftService = ImplSiftService.INSTANCE;
        logo.setImage(new Image("file:amaylogo.png"));
        Logger.info("Login scene loaded");
    }

    @FXML
    void loginButtonClicked(ActionEvent event)  {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Connection connection=null ;

        try{

            connection = SQLiteConnection.INSTANCE.getConnection();
        User user=DBUserRepo.getInstance().readUserByUserId(connection, username);

        
        if (password.equals(user.getPassword())){
            Logger.info("Login successful for user: {}",username);
            SystemConfig.getInstance().setCurrentUser(user);
            siftService.startShift(user);
            UserPrivilegeDto userPrivilegeDto=DBUserRepo.getInstance().getUserPrivilegeById(connection, user.getUserId());
            UserPrivilege userPrivilege=UserMapper.mapToUserPrivilege(userPrivilegeDto);
            agent.setUserPrivilege(userPrivilege);
            this.showHomeScene(agent);
//            PeripheralMonitor peripheralMonitor = new PeripheralMonitor();
//            ImpDeviceStatusListener impDeviceStatusListener = new ImpDeviceStatusListener();
//            peripheralMonitor.addDeviceStatusListener(impDeviceStatusListener);
//            peripheralMonitor.startMonitoring();
            final int SHIFT_TIME = EnvFile.getShiftTimePeriod();

            // Schedule a task to get shift complete
            Logger.info("Shift time period: {}", SHIFT_TIME);
//            if(SHIFT_TIME<=0){
//                Logger.error("Shift time period is less than or equal to 0");
//            }else {
//                Executors.newSingleThreadScheduledExecutor().schedule(() -> {
//
//
//                    Logger.info("Shift is scheduled  for {} minutes", SHIFT_TIME);
//
//                    Platform.runLater(() -> {
//                        // Get the current stage
//                        Stage stage = (Stage) Stage.getWindows().stream()
//                                .filter(Window::isShowing)
//                                .findFirst()
//                                .orElse(null);
//
//                        if (stage != null) {
//                            try {
//                                siftService.endOfShift(EOSType.TIME_OUT); // End the shift
//
//                                FXMLLoader fxmlLoader = ViewFactory.getLogin();
//                                Parent root = fxmlLoader.load();
//                                stage.getScene().setRoot(root);
//                                stage.show();
//
//                            } catch (IOException e) {
//                                Logger.error("Error loading login scene: {}", e.getMessage());
//                            }
//                        } else {
//                            Logger.error("Error: No active stage found");
//                        }
//                    });
//
//                    Logger.info("Shift completed");
//                }, SHIFT_TIME, TimeUnit.MINUTES);
//            }




        } else {
            Logger.warn("Login failed for user: {}",username);
            messageLabel.setText("Login failed");
        }
        }catch(SQLException e){
            messageLabel.setText("Login failed due to database error");
            Logger.error("Error reading user: {}", e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            Logger.debug("Closing connection");
            SQLiteConnection.INSTANCE.releaseConnection(connection);
        }

        Logger.debug("at the end of login");

    }

    public void showHomeScene(Agent agent) {
        try {
            // Create the FXMLLoader instance
            FXMLLoader loader = ViewFactory.getHome();
            EquipmentPrivilege equipmentPrivilegeDto = EquipmentMapper.mapToEquipmentPrivilege(agent.getEquipmentPrivilegeDto());
            agent.setEquipmentPrivilege(equipmentPrivilegeDto);
            // Set the controller factory (if needed)
            loader.setControllerFactory(param -> new Controller(agent));
            Scanner sc=new Scanner(System.in);
            new Thread(() -> {
                while (true) {
                    System.out.println("Enter the value for qrTicketAnalysis ");
                    boolean b = sc.nextBoolean();
                    equipmentPrivilegeDto.setQrFreeTicket(b);
                }
            }).start();

            // Load the FXML file
            Parent homeRoot = loader.load();

            // Get the main stage from the current scene
            Stage mainStage = (Stage) messageLabel.getScene().getWindow();

            // Set the loaded root node as the new root node of the existing scene
            mainStage.getScene().setRoot(homeRoot);
        } catch (IOException e) {
            Logger.error("Error loading home scene: {}", e.getMessage());
        }
    }




}
