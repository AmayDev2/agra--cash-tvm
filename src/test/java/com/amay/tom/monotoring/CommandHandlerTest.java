//package com.amay.tom.monotoring;
//
//import com.amay.tom.grpc.monotoring.CommandHandler;
//import com.amay.tom.service.tom.ApplicationService;
//import com.amay.tom.systemcontrole.SystemControl;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//class CommandHandlerTest {
//    private CommandHandler commandHandler;
//    org.network.monitorandcontrol.CommandType commandType;
//    @BeforeEach
//    void setUp() {
//        commandHandler = new CommandHandler(new ApplicationService(new SystemControl()));
//    }
//
//    @Test
//    void testDeviceInfoCommandHandler() {
//        assertNotNull(commandHandler);
//        commandType = org.network.monitorandcontrol.CommandType.GET_DEVICE_INFO;
//        sendCommand();
//
//    }
//
//    @Test
//    void testModeControlCommandHandler() {
//        assertNotNull(commandHandler);
//        commandType = org.network.monitorandcontrol.CommandType.MODE_CONTROL;
//        sendCommand();
//
//    }
//
//    @Test
//    void testGetDeviceVersionsCommandHandler() {
//        assertNotNull(commandHandler);
//        commandType = org.network.monitorandcontrol.CommandType.GET_DIVICE_VERSIONS;
//        sendCommand();
//
//    }
//
//    @Test
//    void testGetPeripheralStatusCommandHandler() {
//        assertNotNull(commandHandler);
//        commandType = org.network.monitorandcontrol.CommandType.GET_PERIPHERAL_STATUS;
//        sendCommand();
//
//    }
//
//
//    private void sendCommand(){
//        commandHandler.handleCommand(commandType, null);
//    }
//
//    @AfterEach
//    void tearDown() {
//
//    }
//}