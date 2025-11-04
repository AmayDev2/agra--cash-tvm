package com.amay.tvm.bnr;


import com.amay.tom.service.payment2.impl.CashPayment;
import com.amay.tvm.backend.entity.FinanceOperationEntity;
import com.amay.tvm.backend.enums.FinanceOperation;
import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.coin.CoinModuleInterface;
import com.amay.tvm.coin.model.AmountDetail;
import com.amay.tvm.coin.model.HaveAmountObject;
import com.amay.tvm.coin.model.ReturnableAmountObject;
import com.amay.tvm.coin.service.CoinResponseDecoder;
import com.jxfs.control.IJxfsBaseControl;
import com.jxfs.control.cdr.*;
import com.jxfs.events.IJxfsIntermediateListener;
import com.jxfs.events.JxfsException;
import com.jxfs.events.JxfsIntermediateEvent;
import com.jxfs.events.JxfsOperationCompleteEvent;
import com.jxfs.general.IJxfsConst;
import com.jxfs.general.JxfsDeviceManager;
import com.jxfs.general.JxfsRemoteDeviceInformation;
import com.mei.bnr.Bnr;
import com.mei.bnr.cashunit.DenominationItem;
import com.mei.bnr.consts.error.BnrXfsErrorCode;
import com.mei.bnr.exception.BnrException;
import com.mei.bnr.jxfs.device.*;
import com.mei.bnr.jxfs.device.state.IModuleState;
import com.mei.bnr.jxfs.device.state.MEIModuleStatus;
import com.mei.bnr.jxfs.drivers.BnrUsbDriver;
import com.mei.bnr.jxfs.service.IDirectIOConsts;
import com.mei.bnr.jxfs.service.MEIJxfsCode;
import com.mei.bnr.jxfs.service.SpecificDeviceManager;
import com.mei.bnr.jxfs.service.data.*;
import com.mei.bnr.jxfs.util.ISynchronousOperation;
import com.mei.bnr.jxfs.util.MEIJxfsException;
import com.mei.bnr.jxfs.util.SynchronousJxfsOperationHelper;
import com.mei.bnr.jxfs.xmlrpc.parameters.DirectIOModuleIdParameter;
import com.mei.bnr.jxfs.xmlrpc.parameters.DirectIOModuleSetIdentificationParameters;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.tinylog.Logger;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class BNRIntegration  {

    public static long CASH_IN_AMOUNT = 0;
    public final static String CASH_IN_CURRENCY = "INR";
    public final static int EXPONENT = -2;
    public static long ACCEPTED_AMOUNT = 0;

    public static JxfsATM control;
    public static SynchronousJxfsOperationHelper helper;
    private static IBNRListener bnrListener;

    public static void caps() throws JxfsException {

        Logger.tag(LoggerTag.APP).debug(control.getCapabilities().toString());

    }

    public  static Bnr bnr= new Bnr();

    private static boolean isAllowed;

    //TODO: FIX THIS METHOD
 public static void cancel(boolean empty) throws JxfsException {
     if(!isCancelAllowed)return;
     isAllowed=false;
     bnrListener.informationToShow(BNRMessage.CANCEL_TRYING);

        try {
            control.cancel(1);
            Thread.sleep(100);
        } catch (JxfsException e) {
            Logger.debug(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


     bnrListener.informationToShow(BNRMessage.CANCELED);
        }

    /**************************************************************************
     * main
     *************************************************************************/
    /**
     * Main entry point of IntegrationSample. Demonstrate cycle of cashIn
     * transaction, dispense the change if needed and print some statistics.
     *
     * @param args command line arguments to application
     *             <p>
     *             *************************************************************************
     * @return
     */


    public  static void main(String[] args) {

        initializeBnrController();

        try {
            control.open();


            getSetDateTime();

            makeBnrOperational();
            control.cancel(1);

            dispenseAndPresent(observeCashUnit());
            String input="";
//            do {
                //System.out.print("Insert Command  : ");
//                Scanner scanner= new Scanner(System.in);
//                input=scanner.nextLine();
                input="PAY";
                switch (input) {
                    case "PAY" ->{
                        //System.out.print("Insert amount to pay : ");
                        CASH_IN_AMOUNT=Integer.parseInt(args[0])* 100L;
                        long acceptedAmount = acceptAmount(CASH_IN_AMOUNT);
                        Logger.tag(LoggerTag.APP).debug("You`ve inserted Total"+acceptedAmount+" "+CASH_IN_CURRENCY);
                        if (hasChange(acceptedAmount)) {
                            long amountToChange = acceptedAmount - CASH_IN_AMOUNT;
                            dispenseAndPresent(amountToChange);
                        }//if

                    }
                    case "QUERY"->{
                        observeCashUnit();
                    }
                }
//            }while (input!="q");
            alertStatisticsMessage();
            control.close();
        } catch (JxfsException e) {
            Logger.tag(LoggerTag.APP).debug(e.getMessage());
        } finally {
            disposeBnrController();
        }//try

//        System.exit(0);
//        return true;
    }//main

    public static boolean isConnected(){
        try {
            if(control!=null && helper!=null && control.getStatus().isOpen()  ){
                Logger.tag(LoggerTag.APP).debug(getDeviceStatus().isBusy()+" "+getDeviceStatus().isUserError()+" "+" "+getDeviceStatus().isNoDevice()+" "+getDeviceStatus().isOnLine()+" "+getDeviceStatus().isHardwareError());
//                observeModules();
//                boolean flag=true;
                ArrayList<Integer> modules = getModules();
                for (Integer module : modules) {
                    MEIModuleStatus meiModuleStatus=getStatus(module);
                    IModuleState.ModuleOperationalState moduleOperationalState=meiModuleStatus.getModuleOperationalState();
                    if(!IModuleState.ModuleOperationalState.OS_OPERATIONAL.equals(moduleOperationalState)){
                        return false;
                    }
                    Logger.tag(LoggerTag.APP).debug("Module " + IIdentification.ModuleIdentificationEnum.getById(module)+" "+moduleOperationalState+" "+meiModuleStatus.getErrorCodeDescription());
                }
                return true;
            }
        } catch (JxfsException e) {
//            bnrClose();
           Logger.tag(LoggerTag.APP).error("IsConnected Error : "+e.getErrorCode() +"  "+e.getMessage());
        }
        return false;
    }

    public static boolean  bnrOpen(){
    boolean status=false;
        try {
        initializeBnrController();
            control.open();
            getSetDateTime();
            makeBnrOperational();
            control.cancel(1);
            observeCashUnit();
            status=true;
        } catch (JxfsException e) {
            e.printStackTrace();
        }
        return status;
    }



    public static boolean  bnrClose(){
        boolean status=false;
        try {
            initializeBnrController();
            control.cancel(1);
            control.close();
            status=true;
        } catch (JxfsException e) {
            e.printStackTrace();

        }
        return status;

    }


    public static AcceptAmountResponse cashIn(CashPayment paymentInstance,int amount, IBNRListener listener) {
        BNRIntegration.bnrListener=listener;
        isCancelAllowed=true;
        isAllowed=true;
        helper.resetDisconnected();
        try {
            endCashInTransaction();
            haveAmountObject=getBnrHaveAmountObject();
        } catch (JxfsException e) {
            e.printStackTrace();
        }
        ACCEPTED_AMOUNT=0;
        subscribeEvents();
        //System.out.print("Insert amount to pay : ");
         CASH_IN_AMOUNT=amount* 100L;
        AcceptAmountResponse acceptedAmount=new AcceptAmountResponse();
        acceptedAmount.amountToPay=CASH_IN_AMOUNT;

        try {
            acceptedAmount=acceptAmountV2(CASH_IN_AMOUNT, acceptedAmount);
        } catch (JxfsException e) { // IF BNR DISCONNECTED or cash in could  not start
            listener.setStatus(BNRStatus.FAILED);
            return acceptedAmount;
        }
        Logger.tag(LoggerTag.APP).debug("You`ve inserted Total "+acceptedAmount+"} {"+CASH_IN_CURRENCY);
        long amountToChange = (acceptedAmount.acceptedAmount-acceptedAmount.coinChangedAmount) - CASH_IN_AMOUNT;
        if ( !acceptedAmount.isRollback() && amountToChange>0) {

             bnrListener.compareTotalAmountAndChange((int)acceptedAmount.acceptedAmount, (int) (acceptedAmount.acceptedAmount - CASH_IN_AMOUNT));
                try {
                 bnrListener.informationToShow(BNRMessage.COLLECT_NOTES+amountToChange/100);
                 dispenseAndPresent(amountToChange);
                 // updateToAdd denomination
                    acceptedAmount.dispensedAmount=amountToChange;

                 //TODO: IF NOT HAVE CHANGE THEN TVM SLIP
                } catch (JxfsException e) {
                    listener.setStatus(BNRStatus.FAILED);
                    Logger.tag(LoggerTag.APP).error("Dispense Error : {}",e.getMessage());
                }
        }
        if(acceptedAmount.getCoinChangedAmount()>0) {
            CoinResponseDecoder.CoinModuleDispenseResponse coinModuleResponse =
                    acceptedAmount.getCompletableFuture().join(); // unchecked exceptions
            Logger.tag(LoggerTag.APP).info("Blocking coin response: {}", coinModuleResponse);
            acceptedAmount.setActualCoinChangedAmount((coinModuleResponse.amountDispensed) * 100L);
        }

        return acceptedAmount;
    }//main


    private static void cancelWaiting()  {
        try {
            bnr.cancelWaitingCashTaken();
        } catch (BnrException e) {
            Logger.tag(LoggerTag.APP).error("Cancel waiting cash taken error : {}",e.getMessage());
        }

    }

    private static void updateDenomination(Vector denominationInfo) throws JxfsException {
        var event=helper.run(new ISynchronousOperation() {
            public int run(JxfsATM control) throws JxfsException {
                return control.updateDenominations(denominationInfo);
            }//run
        });
        if (((JxfsOperationCompleteEvent) event).getResult() == IJxfsConst.JXFS_RC_SUCCESSFUL) {
            Set<Integer> list=new HashSet<>();
            vector.stream().filter(JxfsDenominationInfo::isEnableDenomination).forEach(x->{
                //System.out.print(x.getCashType().getValue()/100 +" , ");
                list.add(x.getCashType().getValue()/100);
            });

            bnrListener.setAllowedNotes(list.stream().sorted().toList());
        }

        Logger.tag(LoggerTag.APP).debug("Update Denomination Info : "+event.getResult());


    }
    private static Vector<MEIDenominationInfo> vector;

    private static void queryDenomination(long amount) throws JxfsException {

        if(vector==null) {
            var event = helper.run(new ISynchronousOperation() {
                public int run(JxfsATM control) throws JxfsException {
                    return control.queryDenominations();
                }//run
            });

            vector= (Vector<MEIDenominationInfo>) event.getData();
        }

        mark(amount);
        updateDenomination(vector);
    }

    private static void mark(long amount){
        vector.sort(Comparator.comparingInt(a -> a.getCashType().getValue()));
        Logger.tag(LoggerTag.APP).info("Amount to pay : {}", amount);
        vector.forEach(a -> Logger.tag(LoggerTag.APP).info(String.valueOf(a.getCashType().getValue()+" : "+a.isEnableDenomination())));

        long note=amount;
        for(MEIDenominationInfo x:vector){
            if(note==amount && x.getCashType().getValue()>amount){
                note=x.getCashType().getValue();
            }
            x.setEnableDenomination(x.getCashType().getValue()<=note);
        }
        // TODO: check for maximum amount denomination
        int maxDenomination=0;
        int index=-1;
        for(MEIDenominationInfo x:vector){
            if(x.isEnableDenomination()){
                index++;
                maxDenomination= Math.toIntExact(x.getCashType().getValue() - amount); //in PAISA
            }
        }
        if(maxDenomination>0) {
                if(!isDenominationalPossible(maxDenomination)){
                    bnrListener.informationToShow(BNRMessage.EXACT_AMOUNT_TO_ENTER);
                    Logger.tag(LoggerTag.APP).info("Denomination not  possible for max denomination : {}", maxDenomination);
                    for (MEIDenominationInfo x : vector) {
                        x.setEnableDenomination(x.getCashType().getValue()<=amount);
                    }
                }else {
                    Logger.tag(LoggerTag.APP).debug("Denomination possible for max denomination : {}", maxDenomination);
                }
        }

        Logger.tag(LoggerTag.APP).debug("Marking denomination for amount final order : {}", amount);
        vector.forEach(a -> Logger.tag(LoggerTag.APP).info(String.valueOf(a.getCashType().getValue()+" : "+a.isEnableDenomination())));

    }

    private static boolean isDenominationalPossible(int maxDenomination) {
        return CoinModuleInterface.INSTANCE.isDenominationPossibleAll(haveAmountObject.amountDetailList,maxDenomination/100);

    }

    /****************************************************************************
     * initializeBnrController
     ***************************************************************************/
    /**
     * Initialize control and helper to use jxfs actions.
     *
     ***************************************************************************/
    public static void initializeBnrController() {
        IDeviceProvider driver = null;
        String deviceName = null;

        driver = BnrUsbDriver.getInstance();

        deviceName = getDeviceNameFromBnrUsbDriver();

        initControl(driver, deviceName);

        subscribeEvents();
    }//initializeBnrController

    /****************************************************************************
     * getDeviceNameFromBnrUsbDriver
     ***************************************************************************/
    /**
     * method to obtain a device from the BnrUsbDriver.
     *
     * @return device name
     *
     ***************************************************************************/
    private static String getDeviceNameFromBnrUsbDriver() {
        String deviceName = null;
        IDevice device = null;

        BnrUsbDriver bnrUsbDriver = BnrUsbDriver.getInstance();

        if (isContainsDeviceNames(bnrUsbDriver)) {
            deviceName = getDeviceName(bnrUsbDriver);
            device = bnrUsbDriver.getByName(deviceName);
        }//if

        if (isBnrNotConnected(device)) {
            System.err.println("No device found or device not a BNR");
            throw new IllegalStateException();
        }//if

        return deviceName;
    }//getDeviceNameFromBnrUsbDriver

    /****************************************************************************
     * isContainsDeviceNames
     ***************************************************************************/
    /**
     * Checks that usb driver contains names of devices
     *
     * @param bnrUsbDriver driver
     * @return true or false, depending of name availability
     *
     ***************************************************************************/
    private static boolean isContainsDeviceNames(BnrUsbDriver bnrUsbDriver) {
        return bnrUsbDriver.getDeviceNames().size() > 0 ? true : false;
    }//isContainsDeviceNames

    /****************************************************************************
     * getDeviceName
     ***************************************************************************/
    /**
     * get device name from bnrUsbDriver
     *
     * @param bnrUsbDriver driver
     * @return name of device
     *
     ***************************************************************************/
    private static String getDeviceName(BnrUsbDriver bnrUsbDriver) {
        return bnrUsbDriver.getDeviceNames().iterator().next();
    }//bnrUsbDriver

    /****************************************************************************
     * isBnrNotConnected
     ***************************************************************************/
    /**
     * Checks that Bnr device not connected
     *
     * @param device that gets from usbDriver
     * @return TRUE if Bnr NOT connected and FALSE otherwise
     *
     ***************************************************************************/
    private static boolean isBnrNotConnected(IDevice device) {
        boolean deviceNotExist = (device == null) ? true : false;
        boolean deviceNotBnr   = (device.getDeviceType() != DeviceType.BNR) ? true : false;

        return deviceNotExist || deviceNotBnr;
    }//isBnrNotConnected

    /****************************************************************************
     * initControl
     ***************************************************************************/
    /**
     * Initializes control and helper for access to bnr
     *
     * @param driver for bnr
     * @param deviceName of bnr
     *
     ***************************************************************************/
    private static void initControl(IDeviceProvider driver, String deviceName) {
        try {
            control = initializeAndGetJxfsControl(driver, deviceName);
            helper = new SynchronousJxfsOperationHelper(control);

        } catch (JxfsException e) {
            System.err.println("JxfsException occured in JxfsDeviceManager.initialize().");
            System.err.println(e.toString());
        } catch (Exception e) {
            System.err.println("Exception occured in JxfsDeviceManager.initialize().");
            System.err.println(e.toString());
        }//try
    }//initControl

    /****************************************************************************
     * initializeAndGetJxfsControl
     ***************************************************************************/
    /**
     * Initialize JxfsDeviceManager and obtain a JxfsATM Control for the
     * device identified by the given name and driver.
     *
     * @param driver for bnr
     * @param deviceName of bnr
     * @return a JxfsATM control.
     * @throws JxfsException
     *           if an error occurred.
     *
     ***************************************************************************/
    private static JxfsATM initializeAndGetJxfsControl(IDeviceProvider driver, String deviceName) throws JxfsException {

        JxfsDeviceManager.getReference().initialize(
                "com.mei.bnr.jxfs.service.SpecificDeviceManager,"
                        + "workstation,"
                        + "jxfsClient,"
                        + "lib");

        SpecificDeviceManager.getReference().announce(
                new JxfsRemoteDeviceInformation("workstation", driver.getClass().getName()
                        + "/" + deviceName, "", "com.jxfs.control.cdr.JxfsATM", "com.mei.bnr.jxfs.service.BnrService", "CDR-"
                        + deviceName, "description"));

        final IJxfsBaseControl control = getBaseControl(driver, deviceName);

        if (!isJxfsControl(control)) {
            System.err.println("Not a JxfsATM control.");
            throw new IllegalStateException();
        }//if
        return (JxfsATM) control;
    }//initializeAndGetJxfsControl

    /****************************************************************************
     * getBaseControl
     *************************************************************************/
    /**
     * Returns Jxfs base conrol
     *
     * @param driver for bnr
     * @param deviceName of bnr
     * @return a Jxfs base control.
     * @throws JxfsException
     *           if an error occurred.
     *
     ***************************************************************************/
    private static IJxfsBaseControl getBaseControl(IDeviceProvider driver, String deviceName) throws JxfsException {
        return JxfsDeviceManager.getReference().getDevice(driver.getClass().getName() + "/" + deviceName + "@workstation");
    }//getBaseControl

    /****************************************************************************
     * isJxfsControl
     ***************************************************************************/
    /**
     * Checks that base control is JxfsATM control
     *
     * @param control base control
     * @return true or false if JxfsConrol instance of base control
     *
     ***************************************************************************/
    private static boolean isJxfsControl(IJxfsBaseControl control) {
        return control instanceof JxfsATM ? true : false;
    }//isJxfsControl

    /****************************************************************************
     * subscribeEvents
     ***************************************************************************/
    /**
     * Subscribe to intermediate event, and check how much bills user
     * inserted during cahIn operation.
     *
     * 1) integrator can implement custom Observers for Bnr events and subscribe
     *    it during app initialization. After closing Bnr, integrator should
     *    unsubscribe from Bnr events manually (not implemented in this demo).
     *
     * 2) integrator can implement custom observers on-the-fly using helper and
     *    anonymous classes (implemented in this demo)
     *
     *
     ***************************************************************************/
    private static void subscribeEvents() {

        control.addIntermediateListener(new IJxfsIntermediateListener() {
            public void intermediateOccurred(JxfsIntermediateEvent IE) {
                if(MEIJxfsCode.BNRXFS_I_CDR_SUB_CASH_IN.getCode()==IE.getReason() && IE.getData() instanceof MEICashInOrder data) {
//                    Logger.tag(LoggerTag.APP).debug("You`ve inserted(partial) : " + data.getDenomination().getAmount() + " " + CASH_IN_CURRENCY);
//                    bnrListener.acceptedAmount((int) data.getDenomination().getAmount());   //PAISA-> RUPEE : Last inserted amount of note
//                    bnrListener.informationToShow("Inserted Note is of : ₹ "+data.getDenomination().getAmount()/100+"/-");
//                    ACCEPTED_AMOUNT+=data.getDenomination().getAmount();
//                    mark(CASH_IN_AMOUNT-ACCEPTED_AMOUNT);
//                    Logger.tag(LoggerTag.APP).debug("Insertable Notes Are :-");
//                    Set<Integer> list=new HashSet<>();
//                    vector.stream().filter(JxfsDenominationInfo::isEnableDenomination).forEach(x->{
//                        //System.out.print(x.getCashType().getValue()/100 +" , ");
//                        list.add(x.getCashType().getValue()/100);
//                    });
//
//                    bnrListener.setAllowedNotes(list.stream().toList());
                }else  if(MEIJxfsCode.XFS_I_CDR_INPUT_REFUSED.getCode()==IE.getReason() ) {
                    //
                    Logger.tag(LoggerTag.APP).debug("Refused to accept");
                    try {
                        bnrListener.informationToShow(BNRMessage.REFUSE_TO_ACCEPT);
                    }catch (Exception e){
                        Logger.tag(LoggerTag.APP).error("Refuse to accept error : "+e.getMessage());
                    }
                }else {
                    Logger.tag(LoggerTag.APP).debug("Inter mediate event "+IE.getData()+" : "+IE.getReason());
                }
            }//intermediateOccurred
        });
    }//subscribeEvents

    /****************************************************************************
     * getSetDateTime
     ***************************************************************************/
    /**
     * An example method, how to set up date to bnr. We get current OS time,
     * set it into bnr and print to the terminal.
     *
     * @throws JxfsException
     *                 if an error occurred.
     *
     ***************************************************************************/
    private static void getSetDateTime() throws JxfsException {
        Logger.tag(LoggerTag.APP).debug("Set Date to the BNR");
        Date result = null;

        setCurrentDateTime();
        result = getBnrDateTime();

        Logger.tag(LoggerTag.APP).debug("Date from the BNR: " + result);
    }//getSetDateTime

    /****************************************************************************
     * setCurrentDateTime
     ***************************************************************************/
    /**
     * Takes date from OS and, using helper, set it into the bnr
     *
     * @throws JxfsException
     *                 if an error occurred.
     *
     ***************************************************************************/
    public static void setCurrentDateTime() throws JxfsException {
        final Date date = GregorianCalendar.getInstance().getTime();

        // Set Date Time.
        helper.run(new ISynchronousOperation() {
            public int run(JxfsATM control) throws JxfsException {
                return control.setDateTime(date);
            }//run
        });
    }//setCurrentDateTime

    /****************************************************************************
     * getBnrDateTime
     ***************************************************************************/
    /**
     * Gets date from bnr
     *
     * @return date from bnr
     * @throws JxfsException
     *                 if an error occurred.
     *
     ***************************************************************************/
    public  static Date getBnrDateTime() throws JxfsException {
        Date result = null;
        JxfsOperationCompleteEvent event = null;

        // Run getTime operation
        event = helper.run(new ISynchronousOperation() {
            public int run(JxfsATM control) throws JxfsException {
                return control.getDateTime();
            }//run
        });

        // Check and return result
        if (event.getResult() == IJxfsConst.JXFS_RC_SUCCESSFUL) {
            result = (Date) event.getData();
        } else {
            throw new JxfsException(event.getResult());
        }//if
        return result;
    }//getBnrDateTime

    /****************************************************************************
     * makeBnrOperational
     ***************************************************************************/
    /**
     * If bnr status is offline, set it online
     * @throws JxfsException
     *                  if an error occurred.
     ***************************************************************************/
    private static void makeBnrOperational() throws JxfsException {
        JxfsDeviceStatus deviceStatus = getDeviceStatus();
        Logger.tag(LoggerTag.APP).debug("Device status : "+deviceStatus.isBusy());
//        if (deviceStatus.isBusy()) {
        resetBnr();
//        }


    }//makeBnrOperational

    /****************************************************************************
     * getDeviceStatus
     ***************************************************************************/
    /**
     * If bnr status is offline, set it online
     *
     * @return device status
     * @throws JxfsException
     *                  if an error occurred.
     ***************************************************************************/
    public static JxfsDeviceStatus getDeviceStatus() throws JxfsException {
        MEIBnrStatus status = null;

        // Run get status operation
        JxfsOperationCompleteEvent event = helper.run(new ISynchronousOperation() {
            public int run(JxfsATM control) throws JxfsException {
                return control.directIO(IDirectIOConsts.JXFS_O_MEI_CDR_GET_STATUS, null);
            }//run
        });

        // Check and return result
        if (event.getResult() == IJxfsConst.JXFS_RC_SUCCESSFUL) {
            status = (MEIBnrStatus) event.getData();
        } else {
            throw new JxfsException(event.getResult());
        }//if
        return status.getDeviceStatus();
    }//getDeviceStatus

    /****************************************************************************
     * startCashInTransaction
     ***************************************************************************/
    /**
     * Reset Bnr
     *
     * @throws JxfsException
     *                  if an error occurred.
     ***************************************************************************/
    public static void resetBnr() throws JxfsException {

        control.reset();


    }//resetBnr

    private static final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
    private static ScheduledFuture<?> countdownFuture;

    public static void timeoutStart() {
        int countdownSeconds = 10;

        // Countdown task: prints remaining seconds every 1 second
        countdownFuture = scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            int remaining = countdownSeconds;

            //@Override
            public void run() {
                if (remaining > 0) {
                    Logger.tag(LoggerTag.APP).debug("\r Time remaining: " + remaining + " seconds");
                    remaining--;
                } else {
                    countdownFuture.cancel(false); // Stop the countdown
                }
            }
        }, 0, 1, TimeUnit.SECONDS);

        // Actual rollback task: executes after 5 seconds
        scheduledExecutorService.schedule(() -> {
            try {
                Logger.tag(LoggerTag.APP).debug("Executing rollback...");
                cashInRollback();
            } catch (JxfsException e) {
                throw new RuntimeException(e);
            }
        }, countdownSeconds, TimeUnit.SECONDS);
    }

    public static void timeoutStop() {
        if (countdownFuture != null && !countdownFuture.isDone()) {
            countdownFuture.cancel(true);
        }
        scheduledExecutorService.shutdownNow(); // Stops all tasks
    }

    /****************************************************************************
     * acceptAmount
     ***************************************************************************/
    /**
     * Typical cashIn transaction:
     * 1. Start cashIn transaction
     * 2. Ask user insert amount of money
     * 3. Check if change is needed
     * 4. Check if bnr can change money
     * 5. If Bnr can`t change it - rollback to user and ask lesser bills
     * 6. End transaction
     *
     * @param amount of money
     * @return inserted amount of money
     * @throws JxfsException
     *                  if an error occurred.
     ***************************************************************************/
    public static long acceptAmount(long amount) throws JxfsException {
        queryDenomination(amount);
        MEICashInOrder data = null;
        long insertedAmount = 0;
        boolean hasChange = false;

        startCashInTransaction();

        while ((insertedAmount != amount) && !hasChange) {
            Logger.tag(LoggerTag.APP).debug("Please insert " + CASH_IN_AMOUNT + " " + CASH_IN_CURRENCY + ".");
            data = cashIn(CASH_IN_AMOUNT, CASH_IN_CURRENCY);
            insertedAmount = data.getDenomination().getAmount();
            int x=control.queryCashUnit();
            Logger.tag(LoggerTag.APP).debug("Inserted Amount : "+insertedAmount +" "+amount);
            // Check if change needed
            if (insertedAmount > amount) {
                long requiredChange = insertedAmount - amount;
                hasChange = isDenominational(requiredChange); // do i have exchange?
                // Return money, if bnr can't change it
                if (!hasChange) {
                    Logger.tag(LoggerTag.APP).debug("Unfortunately BNR can`t change this amount of bills "+ requiredChange);
                    if(true) { //TODO: if coin module don't have any  change  process for role back
//                        timeoutStop();
                        cashInRollback();
                    }
                }//if

            }//if
        }//while

//        timeoutStop();
        endCashInTransaction();
        return insertedAmount;
    }//acceptAmount

    public static void emptyRecycler() {
        try {
            control.empty(new Vector(){});
        } catch (JxfsException e) {
            Logger.tag(LoggerTag.APP).error(e.getMessage());
        }
    }

    private static final int MAX_CASH_IN_ATTEMPT_M=15;
    //@Override
    public static void bnrLoad(BNRListenerLoad bnrListenerLoad) {
        try {
            bnrListener=bnrListenerLoad;
            isCancelAllowed=true;
            isAllowed=true;
            helper.resetDisconnected();
            try {
                endCashInTransaction();
                haveAmountObject=getBnrHaveAmountObject();
            } catch (JxfsException e) {
                e.printStackTrace();
            }

            if(vector==null) {
                var event = helper.run(new ISynchronousOperation() {
                    public int run(JxfsATM control) throws JxfsException {
                        return control.queryDenominations();
                    }//run
                });

                vector= (Vector<MEIDenominationInfo>) event.getData();
            }

            for(MEIDenominationInfo x:vector){
                x.setEnableDenomination(true);
            }

            helper.run(new ISynchronousOperation() {
                public int run(JxfsATM control) throws JxfsException {
                    return control.updateDenominations(vector);
                }//run
            });

            startCashInTransaction();
            queryDenomination(10000);
//            cashIn(0,"INR");
            for(int cashInCount=0;cashInCount<MAX_CASH_IN_ATTEMPT_M;cashInCount++) {
                MEICashInOrder data = cashInOneByOne(1, CASH_IN_CURRENCY);
                Logger.tag(LoggerTag.APP).debug("You`ve inserted(partial) : " + data.getDenomination().getAmount() + " " + CASH_IN_CURRENCY);
                bnrListener.acceptedAmount((int) data.getDenomination().getAmount());   //PAISA-> RUPEE : Last inserted amount of note
                bnrListener.informationToShow("Inserted Note is of : ₹ " + data.getDenomination().getAmount() / 100 + "/-");
            }
        } catch (JxfsException e) {
            Logger.tag(LoggerTag.APP).error("Cash in start error {}", e.getMessage());

        }
        disableCancel();



    }

    public static void cancel(){
        try {
            control.cancel(1);
            Thread.sleep(100);
        } catch (JxfsException | InterruptedException e) {
            Logger.debug(e.getMessage());
        }
    }

    //@Override
    public static void bnrLoadRollback() {

        try {
            var event= helper.run(new ISynchronousOperation() {
                public int run(JxfsATM control) throws JxfsException {
                    return control.cashInRollback();
                }//run
            });
            if(event.getResult()== IJxfsConst.JXFS_RC_SUCCESSFUL){
                bnrListener.setStatus(BNRStatus.FAILED);
                cancelWaitingCashTaken(5);
            }
        } catch (JxfsException ex) {
            Logger.tag(LoggerTag.APP).error("Cash in rollback error {}", ex.getMessage());
        }
    }


    public static void bnrLoadCommit() {
        try {
            endCashInTransaction();
            bnrListener.setStatus(BNRStatus.SUCCESS);
        } catch (JxfsException e) {
            Logger.tag(LoggerTag.APP).error("Cash in end error {}", e.getMessage());
        }

    }

    public static void bnrUnload() {

    }

    //
    public static void bnrSetDepositZero() throws BnrException {
            bnr.resetCashboxCuContent(true);

    }

    //@Override
    public static int bnrUnload(String rcyId) {
        int result=0;
        try {
            result = bnr.empty(rcyId, false);
        } catch (BnrException e) {
            Logger.tag(LoggerTag.APP).error("BNR Obj {}", e.getMessage());
        }
        Logger.tag(LoggerTag.APP).debug("Unload Recycler "+rcyId+" Result : "+result);
        return result;

    }

    //@Override
    public static void cancelTimeout() {
        try {
            bnr.cancelWaitingCashTaken();
        } catch (BnrException e) {
            Logger.tag(LoggerTag.APP).error("Waiting Timeout Error {}",e.getMessage());
        }
    }

    private static void instanceCancelTimeout(){

        pauseTransition.jumpTo(pauseTransition.getTotalDuration());
    }

    private static PauseTransition pauseTransition=null;

    private static void cancelWaitingCashTaken(int seconds){
        if(pauseTransition!=null){
            pauseTransition.stop();
        }
        pauseTransition= new PauseTransition(Duration.seconds(seconds));
        pauseTransition.setOnFinished(event -> {
            try {
                bnr.cancelWaitingCashTaken();
            } catch (BnrException e) {
                Logger.tag(LoggerTag.APP).error("Waiting Timeout Error {}",e.getMessage());
            }
        });
        pauseTransition.play();

    }

    //@Override
    public static int bnrUnloadRecycler() {
        int result=0;
        Vector<String> rcyIds=new Vector<>();
        rcyIds.add("RE3(100)");
        rcyIds.add("RE4(50)");
        rcyIds.add("RE5(20)");
        rcyIds.add("RE6(10)");
        try {
            result = control.empty(rcyIds);
        } catch (JxfsException e) {
        for(String rcyId:rcyIds) {
                Logger.tag(LoggerTag.APP).error(e.getMessage());
                result=bnrUnload(rcyId);
            }
        }
        return result;

    }

    public static void reBootBnr() {

    }

    //@Override
    public Object bnrModuleStatus() {
        return null;
    }

    @Data
    @RequiredArgsConstructor
    public static class AcceptAmountResponse{
        private long amountToPay;
        private long acceptedAmount;
        private boolean status;
        private boolean rollback;
        private long coinChangedAmount;  //PAISA
        private long actualCoinChangedAmount; //PAISA
        private long dispensedAmount;
        private CompletableFuture<CoinResponseDecoder.CoinModuleDispenseResponse> completableFuture;

    }

    static final  int MAX_CASH_IN_ATTEMPT=20;
    public static AcceptAmountResponse acceptAmountV2(long amount,AcceptAmountResponse acceptAmountResponse) throws JxfsException {

        MEICashInOrder data = null;
        long insertedAmount = 0;
        boolean hasChange = false;
        startCashInTransaction();

        try {
            for(int cashInCount=0; isAllowed && cashInCount<MAX_CASH_IN_ATTEMPT && insertedAmount<amount;cashInCount++) {
                queryDenomination(amount-insertedAmount);
                data = cashInOneByOne(1, CASH_IN_CURRENCY);
                insertedAmount+= data.getDenomination().getAmount(); //after cashIn function completion it gives total amount Accepted
                acceptAmountResponse.setAcceptedAmount(insertedAmount);
                Logger.tag(LoggerTag.APP).debug("You`ve inserted(partial) : " + data.getDenomination().getAmount() + " " + CASH_IN_CURRENCY);
                bnrListener.acceptedAmount((int) data.getDenomination().getAmount());   //PAISA-> RUPEE : Last inserted amount of note
                bnrListener.informationToShow("Inserted Note is of : ₹ " + data.getDenomination().getAmount() / 100 + "/-");
            }
            disableCancel();

            if(insertedAmount<amount){
                throw new RuntimeException("Can't process input amount is less than required");
            }

            // Check if change needed
            if (insertedAmount > amount) {
                long requiredChange = insertedAmount - amount;
                acceptAmountResponse.setAcceptedAmount(insertedAmount);
                hasChange = isDenominational(requiredChange); // do I have exchange?
                int maxChangeAvailable = 0;
                // Return money, if bnr can't change it
                if (!hasChange) {
//                    --->>> get maximum change available in bnr
                    ReturnableAmountObject returnableAmountObject = CoinModuleInterface.INSTANCE.getMaxChangeableAmount(haveAmountObject, requiredChange / 100);
                    maxChangeAvailable = returnableAmountObject.totalAmount * 100;

                    int changeNeeded = (int) requiredChange - maxChangeAvailable;
                    Logger.tag(LoggerTag.APP).error("Unfortunately BNR can`t change this amount of bills " + changeNeeded);
                    bnrListener.informationToShow(BNRMessage.COLLECT_COINS+changeNeeded/100);

                    acceptAmountResponse.setCoinChangedAmount(changeNeeded);
                    acceptAmountResponse.setStatus(true);

                    acceptAmountResponse.setCompletableFuture(CompletableFuture.supplyAsync(() -> {
                                        CoinResponseDecoder.CoinModuleDispenseResponse dispenseResponse =
                                                CoinModuleInterface.INSTANCE.dispense(changeNeeded / 100);
                                        Logger.tag(LoggerTag.APP).info(dispenseResponse.toString());

                                        return dispenseResponse;
                                    }));

                } else {
                    acceptAmountResponse.setStatus(true);
                }
            } else {
                acceptAmountResponse.setStatus(true);
            }
        }catch (Exception e) {
            acceptAmountResponse.setStatus(false);
            try {
                if(acceptAmountResponse.getActualCoinChangedAmount()<=0) {
                    cashInRollback();
                    acceptAmountResponse.setAcceptedAmount(0);
                    acceptAmountResponse.setRollback(true);
                }
            } catch (JxfsException ex) {
                ex.printStackTrace();
            }
            Logger.error("Exception during cashIn : ", e);
        }

        try {
            endCashInTransaction(); // try to put this in finally
            bnrListener.setStatus(BNRStatus.SUCCESS);
        }catch (Exception e){
            Logger.tag(LoggerTag.APP).error("Cash In End Exception {}",e.getMessage());
        }

        return acceptAmountResponse;
    }//acceptAmount

    /****************************************************************************
     * startCashInTransaction
     ***************************************************************************/
    /**
     * Start the cash in transaction with the BNR
     *
     * @throws JxfsException
     *                  if an error occurred.
     ***************************************************************************/
    private static void startCashInTransaction() throws JxfsException {

        control.cancel(1);


        var event=helper.run(new ISynchronousOperation() {
            public int run(JxfsATM control) throws JxfsException {
                return control.cashInStart(IJxfsCDRConst.JXFS_C_CDR_POS_DEFAULT, true);
            }//run
        });



    }//startCashInTransaction

    /****************************************************************************
     * cashIn
     ***************************************************************************/
    /**
     * Move inserted notes to the escrow
     *
     * @param amount required
     * @param currencyCode required
     * @return information about inserted notes
     * @throws JxfsException
     *                  if an error occurred.
     ***************************************************************************/
    private static MEICashInOrder cashIn(long amount, String currencyCode) throws JxfsException {
        MEICashInOrder result = null;
        JxfsOperationCompleteEvent event = null;

//        Vector v=new Vector();
//        v.add(vector.get(6));
//        v.add(vector.get(6));

        final JxfsDenomination denomination = new JxfsDenomination(null, amount, 0);
        final JxfsCurrency jxfsCurrency = new JxfsCurrency(new JxfsCurrencyCode(
                currencyCode == null ? "" : currencyCode), 0);

        // Run cashIn operation
        event = helper.run(new ISynchronousOperation() {
            public int run(JxfsATM deviceControl) throws JxfsException {
                return ((JxfsATM) deviceControl).cashIn(new JxfsCashInOrder(denomination, jxfsCurrency));
            }//run
        });
        // If the result is not successful
        if (event.getResult() != IJxfsConst.JXFS_RC_SUCCESSFUL) {
            Logger.error("CashIn failed with error: {} ", event.getResult());
            throw new MEIJxfsException(event.getResult(), event.getData());
        } else {
            result = (MEICashInOrder) event.getData();
        }//if
        return result;
    }//cashIn



    private static MEICashInOrder cashInOneByOne(long amount, String currencyCode) throws JxfsException {
        MEICashInOrder result = null;
        JxfsOperationCompleteEvent event = null;

        final JxfsDenomination denomination = new JxfsDenomination(null, amount, 0);
        final JxfsCurrency jxfsCurrency = new JxfsCurrency(new JxfsCurrencyCode(
                currencyCode == null ? "" : currencyCode), 0);

        // Run cashIn operation
        event = helper.run(new ISynchronousOperation() {
            public int run(JxfsATM deviceControl) throws JxfsException {
                return ((JxfsATM) deviceControl).cashIn(new JxfsCashInOrder(denomination, jxfsCurrency));
            }//run
        });
        //1021 cancel
//        int JXFS_E_CLOSED = 1002; closed, power cut -> Reset
        // 1020 IO ->  Open
        // OUT of Service -> closed ; inservice ->  Open
        // Maintenance -> tran cancel
        // If the result is not successful
        if (event.getResult() != IJxfsConst.JXFS_RC_SUCCESSFUL) {
            Logger.tag(LoggerTag.APP).error("CashIn failed with error: {} ", event.getResult());
            bnrListener.informationToShow(BNRMessage.REFUSE_TO_ACCEPT);
            throw new MEIJxfsException(event.getResult(), event.getData());
        } else {
            result = (MEICashInOrder) event.getData();
        }//if
        return result;
    }//cashIn

    /****************************************************************************
     * isDenominational
     ****************************************************************************/
    /**
     * Check if device has enough money to change
     *
     * @param amountToDenominate required
     * @return true or false, dependable on denomination operation
     * @throws JxfsException
     *                  if an error occurred.
     ****************************************************************************/
    private static boolean isDenominational(final long amountToDenominate) throws JxfsException {
        final int mixNumber = IJxfsCDRConst.JXFS_C_CDR_MIX_ALGORITHM;
        final Vector<JxfsDenominationItem> denominationItems = null;

        // Run denomination operation
        JxfsOperationCompleteEvent event = helper.run(new ISynchronousOperation() {
            public int run(JxfsATM control) throws JxfsException {
                return control.denominate(mixNumber, new JxfsDenomination(
                        denominationItems, amountToDenominate, 0), new JxfsCurrency(
                        new JxfsCurrencyCode(CASH_IN_CURRENCY), EXPONENT));
            }//run
        });

        // Check result
        //if
        return event.getResult() != IJxfsCDRConst.JXFS_E_CDR_NOT_DISPENSABLE;
    }//isDenominational

    static boolean isCancelAllowed;

    private static void disableCancel(){
        isCancelAllowed=false;
        bnrListener.disableCancelButton();
    }

    /****************************************************************************
     * cashInRollback
     ***************************************************************************/
    /**
     * Roll back the cash in transaction
     *
     * @throws JxfsException
     *                  if an error occurred.
     ***************************************************************************/
    private static void cashInRollback() throws JxfsException {

        bnrListener.informationToShow(BNRMessage.ROLLBACK);
        disableCancel();
        // First reject the notes from escrow
//

        // Retrieve the operationCompleteEvent
       var event= helper.run(new ISynchronousOperation() {
            public int run(JxfsATM control) throws JxfsException {
                return control.cashInRollback();
            }//run
        });


        if (event.getResult() != BnrXfsErrorCode.XFS_SUCCESSFULL) {
            throw new JxfsException(event.getResult());
        }else {
            // ROLL BACK SUCCESS
            bnrListener.setStatus(BNRStatus.FAILED);
        }
    }//cashInRollback

    private static JxfsOperationCompleteEvent reject() throws JxfsException {

        // Retrieve the operationCompleteEvent
        var event=helper.run(new ISynchronousOperation() {
            public int run(JxfsATM control) throws JxfsException {
                return control.reject(true);
            }//run
        });
        return event;
    }//cashInRollback



    /****************************************************************************
     * endCashInTransaction
     ***************************************************************************/
    /**
     * End the cash in transaction with the BNR
     *
     * @throws JxfsException
     *                  if an error occurred.
     ***************************************************************************/
    private static void endCashInTransaction() throws JxfsException {
        Logger.tag(LoggerTag.APP).debug("Edd Cash Transaction : "+ Arrays.toString(new boolean[]{helper.run(new ISynchronousOperation() {
            public int run(JxfsATM control) throws JxfsException {
                return control.cashInEnd();
            }//run
        }).getResult() == BnrXfsErrorCode.XFS_SUCCESSFULL}));

    }//endCashInTransaction

    /****************************************************************************
     * hasChange
     ***************************************************************************/
    /**
     * Check if bnr should give change or not
     *
     * @param insertedAmount amount
     * @return true or not, if inserted amount bigger then required
     ***************************************************************************/
    private static boolean hasChange(long insertedAmount) {
        return (insertedAmount - CASH_IN_AMOUNT) > 0;
    }//hasChange

    /****************************************************************************
     * dispenseAndPresent
     ***************************************************************************/
    /**
     * Dispense and give change to user
     *
     * @param amountToChange required
     * @throws JxfsException
     *                  if an error occurred.
     ***************************************************************************/
    private static void dispenseAndPresent(long amountToChange) throws JxfsException {
        if(amountToChange<=0)return;
        Logger.tag(LoggerTag.APP).debug("Take your change");
        bnrListener.informationToShow(BNRMessage.CHANGE_COLLECT);
        dispense(amountToChange);
        present();
    }//dispenseAndPresent

    /****************************************************************************
     * dispense
     ***************************************************************************/
    /**
     * Dispenses the amount
     *
     * @param amountToChange required
     * @throws JxfsException
     *                  if an error occurred.
     ***************************************************************************/
    private static void dispense(final long amountToChange) throws JxfsException {
        final int mixNumber = IJxfsCDRConst.JXFS_C_CDR_MIX_ALGORITHM;

        // Run dispense operation
        JxfsOperationCompleteEvent event=helper.run(new ISynchronousOperation() {
            public int run(JxfsATM control) throws JxfsException {
                return control.dispense(new JxfsDispenseRequest(mixNumber,
                        new JxfsDenomination(null, amountToChange, 0),
                        new JxfsCurrency(new JxfsCurrencyCode(CASH_IN_CURRENCY), EXPONENT),
                        IJxfsCDRConst.JXFS_C_CDR_POS_DEFAULT));
            }//run
        });

        if(event.getResult() != IJxfsConst.JXFS_RC_SUCCESSFUL) {
            throw new JxfsException(event.getResult());
        }else{
            MEIDispenseOrder meiDispenseOrder= (MEIDispenseOrder) event.getData();
            bnrListener.dispensedAmount(getAmountAndQuantity(meiDispenseOrder.getMEIDenomination().getItems()));
            Logger.tag(LoggerTag.APP).debug("Dispense operation successful");
        }
        //
    }//dispense

    /****************************************************************************
     * present
     ***************************************************************************/
    /**
     * Present dispensed money
     *
     //     * @param amountToChange
     * @throws JxfsException
     *                  if an error occurred.
     ***************************************************************************/
    private static void present() throws JxfsException {

        // Run present operation
        helper.run(new ISynchronousOperation() {
            public int run(JxfsATM control) throws JxfsException {
                return control.present();
            }//run
        });
    }//present

    /*************************************************************************
     * alertStatisticsMessage
     ***************************************************************/
    /**
     * Print statistics message
     *
     *************************************************************************/
    private static void alertStatisticsMessage() {

        Logger.tag(LoggerTag.APP).debug("\n********************************************************************");
        Logger.tag(LoggerTag.APP).debug("STATISTICS");
        Logger.tag(LoggerTag.APP).debug("********************************************************************\n");
    }//alertStatisticsMessage

    /****************************************************************************
     * observeCashUnit
     ***************************************************************************/
    /**
     * Print cashUnit statistics
     *
     * @throws JxfsException
     *                  if an error occurred.
     ***************************************************************************/
    private static int observeCashUnit() throws JxfsException {

        Logger.tag(LoggerTag.APP).debug("\n******************* Cash Units *******************");
//        Logger.tag(LoggerTag.APP).debug(queryCashUnit());
        MEICashUnit cashUnit=queryCashUnit();

        AtomicInteger sum=new AtomicInteger(0);

        cashUnit.getLogicalCashUnits()     // full list
                .subList(5, 9)               // indices 5 (inclusive) … 9 (exclusive) ⇒ elements 5-8
                .forEach(x -> {
                    cashUnit.getPcus().stream().filter(v -> v.getName().equals(x.getPhysicalName()))
                            .filter(x1->x1.getCount()!=0)
                            .findFirst().ifPresent(p -> {
                                Logger.tag(LoggerTag.APP).debug(x.getCashTypeDescription().split(" ")[1]+"*"+p.getCount() + " -> " + Integer.parseInt(x.getCashTypeDescription().split(" ")[1]) * p.getCount()+" : "+x.getPhysicalName());
                                sum.getAndAdd(Integer.parseInt(x.getCashTypeDescription().split(" ")[1]) * p.getCount());
                            });
                });



        Logger.tag(LoggerTag.APP).debug("TOTAL SUM : "+sum.get()/100);
        return sum.get();

    }//observeCashUnit

    static HaveAmountObject haveAmountObject;


    public static HaveAmountObject getBnrHaveAmountObject() throws JxfsException {

        Logger.tag(LoggerTag.APP).debug("\n******************* Cash Units *******************");
//        Logger.tag(LoggerTag.APP).debug(queryCashUnit());
        MEICashUnit cashUnit=queryCashUnit();
        HaveAmountObject bnrHaveAmount=new HaveAmountObject(new ArrayList<>());

        AtomicInteger sum=new AtomicInteger(0);

        cashUnit.getLogicalCashUnits()     // full list
                .subList(5, 9)               // indices 5 (inclusive) … 9 (exclusive) ⇒ elements 5-8
                .forEach(x -> {
                    cashUnit.getPcus().stream().filter(v -> v.getName().equals(x.getPhysicalName()))
                            .filter(x1->x1.getCount()!=0)
                            .findFirst().ifPresent(p -> {
                                Logger.tag(LoggerTag.APP).debug(x.getCashTypeDescription().split(" ")[1]+"*"+p.getCount() + " -> " + Integer.parseInt(x.getCashTypeDescription().split(" ")[1]) * p.getCount()+" : "+x.getPhysicalName());
//                                sum.getAndAdd(Integer.parseInt(x.getCashTypeDescription().split(" ")[1]) * p.getCount());
                                int amount=Integer.parseInt(x.getCashTypeDescription().split(" ")[1])/100;
                                int quantity=p.getCount();  //geting the count RC+escrow
                                bnrHaveAmount.amountDetailList.add(new AmountDetail(amount,amount*quantity,quantity));
                            });
                });

        bnrHaveAmount.amountDetailList.sort(Comparator.comparingInt(AmountDetail::getAmount).reversed());

        Logger.tag(LoggerTag.APP).debug("TOTAL SUM : "+sum.get());
//        bnrHaveAmount.totalAmount=sum.get();
        return bnrHaveAmount;

    }//observeCashUnit

    public static List<FinanceOperationEntity> getAmountAndQuantity(Vector<JxfsDenominationItem> list) throws JxfsException {


        List<FinanceOperationEntity> list2=new ArrayList<>();
        Logger.tag(LoggerTag.APP).debug("\n******************* Cash Units *******************");
        MEICashUnit cashUnit=queryCashUnit();

        cashUnit.getLogicalCashUnits()     // full list
                .subList(5, 9)               // indices 5 (inclusive) … 9 (exclusive) ⇒ elements 5-8
                .forEach(x -> {
                    list.stream().filter(p->x.getNumber()==p.getUnit()).forEach(xd->{
                        int amount=Integer.parseInt(x.getCashTypeDescription().split(" ")[1])/100;
                       list2.add(new FinanceOperationEntity().setQuantity(xd.getCount()).setUnitAmount(amount).setOperationType(FinanceOperation.BNR_DISPENSE));
                    });
                });

        return list2;

    }

    /****************************************************************************
     * queryCashUnit
     ***************************************************************************/
    /**
     * Get the complete state of all physical and logical cash units in the BNR
     *
     * @return data about cashUnits
     * @throws JxfsException
     *                  if an error occurred.
     ***************************************************************************/
    public static MEICashUnit queryCashUnit() throws JxfsException {
        MEICashUnit resultedCashUnit = null;


        // Retrieve the operationCompleteEvent
        JxfsOperationCompleteEvent event = helper.run(new ISynchronousOperation() {
            public int run(JxfsATM control) throws JxfsException {
                return control.queryCashUnit();
            }//run

        });

        // If the result is successful
        if (event.getResult() == IJxfsCDRConst.JXFS_RC_SUCCESSFUL) {
            // Retrieve the MEICashUnit object
            resultedCashUnit = (MEICashUnit) event.getData();
        } else {
            throw new JxfsException(event.getResult());
        }//if
        return resultedCashUnit;
    }//queryCashUnit

    /****************************************************************************
     * observeModules
     ***************************************************************************/
    /**
     * Print Modules statistics
     * @throws JxfsException
     *
     * @throws JxfsException
     *                  if an error occurred.
     ***************************************************************************/
    private static void observeModules() throws JxfsException {
        System.out
                .println("\n******************* Modules *******************");

        ArrayList<Integer> modules = getModules();

        for (Integer module : modules) {
            Logger.tag(LoggerTag.APP).debug("Module " + IIdentification.ModuleIdentificationEnum.getById(module));

            MEIModuleIdentification properties = getIdentification(module);

            // Set Module Name if not Bundler or Cashbox
            if (!(properties.getDescription().contains("Bundler")) && !(properties.getDescription().contains("Cashbox"))) {
                setIdentification(module, "userInfo");
            }//if

            // Save User info to Module
            Logger.tag(LoggerTag.APP).debug(getStatus(module));
        }//for

    }//observeModules

    /****************************************************************************
     * getModules
     ***************************************************************************/
    /**
     * Get the list of all available modules.
     *
     * @return the list of module ids or <code>null</code> if an error occurred.
     * @throws JxfsException
     *           if the command failed
     *
     ***************************************************************************/
    @SuppressWarnings("unchecked")
    private static ArrayList<Integer> getModules() throws JxfsException {
        ArrayList<Integer> modules = null;

        JxfsOperationCompleteEvent eventGetModules = helper
                .run(new ISynchronousOperation() {
                    public int run(JxfsATM control) throws JxfsException {
                        return control.directIO(
                                IDirectIOConsts.JXFS_O_MEI_CDR_GET_MODULES, null);
                    }//run
                });

        if (eventGetModules.getResult() == IJxfsCDRConst.JXFS_RC_SUCCESSFUL) {
            modules = (ArrayList<Integer>) eventGetModules.getData();
        } else {
            throw new JxfsException(eventGetModules.getResult());
        }//if
        return modules;
    }//getModules

    /****************************************************************************
     * getIdentification
     ***************************************************************************/
    /**
     * Get module identification for the given module id.
     *
     * @param moduleId
     *          the given module id.
     * @return a MEIModuleIdentification.
     * @throws JxfsException
     *           if the command failed
     *
     ***************************************************************************/
    private static MEIModuleIdentification getIdentification(final int moduleId) throws JxfsException {
        MEIModuleIdentification moduleIdentification = null;

        JxfsOperationCompleteEvent event = helper.run(new ISynchronousOperation() {
            public int run(JxfsATM control) throws JxfsException {
                return control.directIO(
                        IDirectIOConsts.JXFS_O_MEI_CDR_MODULE_GET_IDENTIFICATION,
                        new DirectIOModuleIdParameter(moduleId));
            }//run
        });

        if (event.getResult() == IJxfsConst.JXFS_RC_SUCCESSFUL) {
            moduleIdentification = (MEIModuleIdentification) event.getData();
        } else {
            throw new JxfsException(event.getResult());
        }//if

        return moduleIdentification;
    }//getIdentification

    /****************************************************************************
     * setIdentification
     ***************************************************************************/
    /**
     * Set the user identification of a module.
     *
     * @param moduleId
     *          the module id
     * @param stringIdentification
     *          the string to be written in the BNR. The maximum length is 255
     *          characters.
     * @throws JxfsException
     *           if the command failed
     *
     ***************************************************************************/
    private static void setIdentification(final int moduleId,
                                          final String stringIdentification) throws JxfsException {
        JxfsOperationCompleteEvent event = helper.run(new ISynchronousOperation() {
            public int run(JxfsATM control) throws JxfsException {
                return control.directIO(
                        IDirectIOConsts.JXFS_O_MEI_CDR_MODULE_SET_IDENTIFICATION,
                        new DirectIOModuleSetIdentificationParameters(moduleId,
                                stringIdentification));
            }//run
        });

        // If the result is not successful
        if (event.getResult() != IJxfsConst.JXFS_RC_SUCCESSFUL) {
            throw new JxfsException(event.getResult());
        }//if
    }//setIdentification

    /****************************************************************************
     * getStatus
     ***************************************************************************/
    /**
     * Get the module state for the given module id.
     *
     * @param moduleId
     *          the given module id.
     * @return a MEIModuleStatus.
     * @throws JxfsException
     *           if the command failed
     ***************************************************************************/
    private static MEIModuleStatus getStatus(final int moduleId) throws JxfsException {
        MEIModuleStatus moduleStatus = null;

        // Retrieve the operationCompleteEvent
        JxfsOperationCompleteEvent event = helper.run(new ISynchronousOperation() {
            public int run(JxfsATM control) throws JxfsException {
                return control.directIO(
                        IDirectIOConsts.JXFS_O_MEI_CDR_MODULE_GET_STATE,
                        new DirectIOModuleIdParameter(moduleId));
            }//run
        });

        // If the result is successful
        if (event.getResult() == IJxfsConst.JXFS_RC_SUCCESSFUL) {
            // Retrieve the MEIModuleStatus object
            moduleStatus = (MEIModuleStatus) event.getData();
        } else {
            throw new JxfsException(event.getResult());
        }//if
        return moduleStatus;
    }//getStatus

    /****************************************************************************
     * disposeBnrController
     ***************************************************************************/
    /**
     * Close bnrController end exit from application
     *
     ***************************************************************************/
    private static void disposeBnrController() {
        JxfsDeviceManager.getReference().shutdown();
    }//disposeBnrController
}//Integra
