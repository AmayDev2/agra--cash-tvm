package com.amay.tvm.bnr;


import com.jxfs.control.IJxfsBaseControl;
import com.jxfs.control.cdr.*;
import com.jxfs.events.IJxfsIntermediateListener;
import com.jxfs.events.JxfsException;
import com.jxfs.events.JxfsIntermediateEvent;
import com.jxfs.events.JxfsOperationCompleteEvent;
import com.jxfs.general.IJxfsConst;
import com.jxfs.general.JxfsDeviceManager;
import com.jxfs.general.JxfsRemoteDeviceInformation;
import com.mei.bnr.consts.error.BnrXfsErrorCode;
import com.mei.bnr.jxfs.device.*;
import com.mei.bnr.jxfs.device.state.MEIModuleStatus;
import com.mei.bnr.jxfs.drivers.BnrUsbDriver;
import com.mei.bnr.jxfs.service.IDirectIOConsts;
import com.mei.bnr.jxfs.service.MEIJxfsCode;
import com.mei.bnr.jxfs.service.SpecificDeviceManager;
import com.mei.bnr.jxfs.service.data.MEIBnrStatus;
import com.mei.bnr.jxfs.service.data.MEICashInOrder;
import com.mei.bnr.jxfs.service.data.MEICashUnit;
import com.mei.bnr.jxfs.service.data.MEIDenominationInfo;
import com.mei.bnr.jxfs.util.ISynchronousOperation;
import com.mei.bnr.jxfs.util.MEIJxfsException;
import com.mei.bnr.jxfs.util.SynchronousJxfsOperationHelper;
import com.mei.bnr.jxfs.xmlrpc.parameters.DirectIOModuleIdParameter;
import com.mei.bnr.jxfs.xmlrpc.parameters.DirectIOModuleSetIdentificationParameters;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.tinylog.Logger;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BNRIntegration {

    public static long CASH_IN_AMOUNT = 5000;
    public final static String CASH_IN_CURRENCY = "INR";
    public final static int EXPONENT = -2;

    public static JxfsATM control;
    public static SynchronousJxfsOperationHelper helper;
    private static IBNRListener bnrListener;

    public static void caps() throws JxfsException {

        System.out.println(control.getCapabilities().toString());

    }

        public static void cancel() throws JxfsException {

        try {
            control.cancel(1);
        } catch (JxfsException e) {
            Logger.debug(e.getMessage());
        }

            bnrListener.informationToShow(BNRMessage.CANCEL_TRYING);
        try {
            cashInRollback();
        } catch (JxfsException e) {
            Logger.debug(e.getMessage());
        }
//            endCashInTransaction();
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


    public  static boolean Main(String[] args) {

        initializeBnrController();

        try {
            control.open();


            getSetDateTime();

            makeBnrOperational();
            control.cancel(1);

            dispenseAndPresent(observeCashUnit());
            String input="";
//            do {
                System.out.print("Insert Command  : ");
//                Scanner scanner= new Scanner(System.in);
//                input=scanner.nextLine();
                input="PAY";
                System.out.println();
                switch (input) {
                    case "PAY" ->{
                        System.out.print("Insert amount to pay : ");
                        CASH_IN_AMOUNT=Integer.parseInt(args[0])* 100L;
                        long acceptedAmount = acceptAmount(CASH_IN_AMOUNT);
                        System.out.println("You`ve inserted Total"+acceptedAmount+" "+CASH_IN_CURRENCY);
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
            System.out.println(e.getMessage());
        } finally {
            disposeBnrController();
        }//try

//        System.exit(0);
        return true;
    }//main

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


    public static boolean cashIn(int amount,IBNRListener listener) {
        BNRIntegration.bnrListener=listener;
//        try {
//            endCashInTransaction();
//            control.open();
//        } catch (JxfsException e) {
//            e.printStackTrace();
//        }


         System.out.print("Insert amount to pay : ");
         CASH_IN_AMOUNT=amount* 100L;
        AcceptAmountResponse acceptedAmount=null;
        try {
            acceptedAmount = acceptAmountV2(CASH_IN_AMOUNT);

        } catch (JxfsException e) {
            listener.setStatus(BNRStatus.FAILED);
            throw new RuntimeException(e);
        }
        System.out.println("You`ve inserted Total "+acceptedAmount+"} {"+CASH_IN_CURRENCY);
        if (hasChange(acceptedAmount.acceptedAmount)) {
             long amountToChange = acceptedAmount.acceptedAmount - CASH_IN_AMOUNT;
             bnrListener.compareTotalAmountAndChange((int)acceptedAmount.acceptedAmount,(int)amountToChange);
                try {
                 dispenseAndPresent(amountToChange);
                } catch (JxfsException e) {
                    listener.setStatus(BNRStatus.FAILED);
                    throw new RuntimeException(e);
                }
   }



        return acceptedAmount.status;
    }//main

    private static void updateDenomination(Vector denominationInfo) throws JxfsException {
        var event=helper.run(new ISynchronousOperation() {
            public int run(JxfsATM control) throws JxfsException {
                return control.updateDenominations(denominationInfo);
            }//run
        });
        if (((JxfsOperationCompleteEvent) event).getResult() == IJxfsConst.JXFS_RC_SUCCESSFUL) {
            Set<Integer> list=new HashSet<>();
            vector.stream().filter(JxfsDenominationInfo::isEnableDenomination).forEach(x->{
                System.out.print(x.getCashType().getValue()/100 +" , ");
                list.add(x.getCashType().getValue()/100);
            });
            bnrListener.setAllowedNotes(list.stream().toList());
        }

        System.out.println("Update Denomination Info : "+event.getResult());


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


        vector.forEach(a -> System.out.println(a.getCashType().getValue()+" "+a.isEnableDenomination()));
        mark(amount);
        updateDenomination(vector);
    }

    private static void mark(long amount){
        vector.sort(Comparator.comparingInt(a -> a.getCashType().getValue()));

        long note=amount;
        for(MEIDenominationInfo x:vector){
            if(note==amount && x.getCashType().getValue()>amount){
                note=x.getCashType().getValue();
            }
            x.setEnableDenomination(x.getCashType().getValue()<=note);
        }
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
                    System.out.println("You`ve inserted(partial) : " + data.getDenomination().getAmount() + " " + CASH_IN_CURRENCY);
                    bnrListener.acceptedAmount((int) data.getDenomination().getAmount());   //PAISA-> RUPEE : Last inserted amount of note
                    bnrListener.informationToShow("Inserted Note is of : ₹ "+data.getDenomination().getAmount()/100+"/-");
                    mark(data.getDenomination().getAmount());
                    System.out.println("Insertable Notes Are :-");
                    Set<Integer> list=new HashSet<>();
                    vector.stream().filter(JxfsDenominationInfo::isEnableDenomination).forEach(x->{
                        System.out.print(x.getCashType().getValue()/100 +" , ");
                        list.add(x.getCashType().getValue()/100);
                    });

                    bnrListener.setAllowedNotes(list.stream().toList());
                }else  if(MEIJxfsCode.XFS_I_CDR_INPUT_REFUSED.getCode()==IE.getReason() ) {
                    //
                    System.out.println("Refused to accept");
                    bnrListener.informationToShow(BNRMessage.REFUSE_TO_ACCEPT);
                }else {
                    System.out.println("Inter mediate event "+IE.getData()+" : "+IE.getReason());
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
        System.out.println("Set Date to the BNR");
        Date result = null;

        setCurrentDateTime();
        result = getBnrDateTime();

        System.out.println("Date from the BNR: " + result);
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
        System.out.println("Device status : "+deviceStatus.isBusy());
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

        helper.run(new ISynchronousOperation() {
            public int run(JxfsATM control) throws JxfsException {
                return control.reset();
            }//run
        });


    }//resetBnr

    private static final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
    private static ScheduledFuture<?> countdownFuture;

    public static void timeoutStart() {
        int countdownSeconds = 10;

        // Countdown task: prints remaining seconds every 1 second
        countdownFuture = scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            int remaining = countdownSeconds;

            @Override
            public void run() {
                if (remaining > 0) {
                    System.out.println("\r Time remaining: " + remaining + " seconds");
                    remaining--;
                } else {
                    countdownFuture.cancel(false); // Stop the countdown
                }
            }
        }, 0, 1, TimeUnit.SECONDS);

        // Actual rollback task: executes after 5 seconds
        scheduledExecutorService.schedule(() -> {
            try {
                System.out.println("Executing rollback...");
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
            System.out.println("Please insert " + CASH_IN_AMOUNT + " " + CASH_IN_CURRENCY + ".");
            data = cashIn(CASH_IN_AMOUNT, CASH_IN_CURRENCY);
            insertedAmount = data.getDenomination().getAmount();
            int x=control.queryCashUnit();
            System.out.println("Inserted Amount : "+insertedAmount +" "+amount);
            // Check if change needed
            if (insertedAmount > amount) {
                long requiredChange = insertedAmount - amount;
                hasChange = isDenominational(requiredChange); // do i have exchange?
                // Return money, if bnr can't change it
                if (!hasChange) {
                    System.out.println("Unfortunately BNR can`t change this amount of bills "+ requiredChange);
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

    @Data
    @RequiredArgsConstructor
    static class AcceptAmountResponse{
        private long acceptedAmount;
        private boolean status;
    }

    public static AcceptAmountResponse acceptAmountV2(long amount) throws JxfsException {
        AcceptAmountResponse acceptAmountResponse=new AcceptAmountResponse();
        queryDenomination(amount);
        MEICashInOrder data = null;
        long insertedAmount = 0;
        boolean hasChange = false;
        startCashInTransaction();

            data = cashIn(CASH_IN_AMOUNT, CASH_IN_CURRENCY);
            insertedAmount = data.getDenomination().getAmount(); //after cashIn function completion it gives total amount Accepted
            int x=control.queryCashUnit();
            Logger.debug("CASH UNIT : "+x);

            // Check if change needed
            if (insertedAmount > amount) {
                long requiredChange = insertedAmount - amount;
                acceptAmountResponse.setAcceptedAmount(insertedAmount);
                hasChange = isDenominational(requiredChange); // do i have exchange?
                // Return money, if bnr can't change it
                if (!hasChange) {
                    System.out.println("Unfortunately BNR can`t change this amount of bills "+requiredChange);
                    if(true) { //TODO: if coin module don't have any  change  process for role back
                        acceptAmountResponse.setStatus(false);
                        cashInRollback();
                    }
                }else{
                    acceptAmountResponse.setStatus(true);
                }
            }

        endCashInTransaction();
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
        if (event.getResult() == IJxfsCDRConst.JXFS_E_CDR_NOT_DISPENSABLE) {
            return false;
        }//if
        return true;
    }//isDenominational

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

        // Retrieve the operationCompleteEvent
       var event= helper.run(new ISynchronousOperation() {
            public int run(JxfsATM control) throws JxfsException {
                return control.cashInRollback();
            }//run
        });


        if (event.getResult() != BnrXfsErrorCode.XFS_SUCCESSFULL) {
            throw new JxfsException(event.getResult());
        }//if
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
        System.out.println("Edd Cash Transaction : "+ Arrays.toString(new boolean[]{helper.run(new ISynchronousOperation() {
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
        System.out.println("Take your change");
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
//        Vector v=new Vector();
//        if(vector!=null) {
//            v.add(vector.get(6));
//            v.add(vector.get(6));
//        }

        // Run dispense operation
        var event=helper.run(new ISynchronousOperation() {
            public int run(JxfsATM control) throws JxfsException {
                return control.dispense(new JxfsDispenseRequest(mixNumber,
                        new JxfsDenomination(null, amountToChange, 0),
                        new JxfsCurrency(new JxfsCurrencyCode(CASH_IN_CURRENCY), EXPONENT),
                        IJxfsCDRConst.JXFS_C_CDR_POS_DEFAULT));
            }//run
        });
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

        System.out.println("\n********************************************************************");
        System.out.println("STATISTICS");
        System.out.println("********************************************************************\n");
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

        System.out.println("\n******************* Cash Units *******************");
//        System.out.println(queryCashUnit());
        MEICashUnit cashUnit=queryCashUnit();

        AtomicInteger sum=new AtomicInteger(0);

        cashUnit.getLogicalCashUnits()     // full list
                .subList(5, 9)               // indices 5 (inclusive) … 9 (exclusive) ⇒ elements 5-8
                .forEach(x -> {
                    cashUnit.getPcus().stream().filter(v -> v.getName().equals(x.getPhysicalName()))
                            .filter(x1->x1.getCount()!=0)
                            .findFirst().ifPresent(p -> {
                                System.out.println(x.getCashTypeDescription().split(" ")[1]+"*"+p.getCount() + " -> " + Integer.parseInt(x.getCashTypeDescription().split(" ")[1]) * p.getCount()+" : "+x.getPhysicalName());
                                sum.getAndAdd(Integer.parseInt(x.getCashTypeDescription().split(" ")[1]) * p.getCount());
                            });
                });



        System.out.println("TOTAL SUM : "+sum.get()/100);
        return sum.get();

    }//observeCashUnit

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
    private static MEICashUnit queryCashUnit() throws JxfsException {
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
            System.out.println("Module " + IIdentification.ModuleIdentificationEnum.getById(module));

            MEIModuleIdentification properties = getIdentification(module);

            // Set Module Name if not Bundler or Cashbox
            if (!(properties.getDescription().contains("Bundler")) && !(properties.getDescription().contains("Cashbox"))) {
                setIdentification(module, "userInfo");
            }//if

            // Save User info to Module
            System.out.println(getStatus(module));
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
