package com.amay.tom;

import com.amay.tom.controller.Controller;
import javafx.fxml.FXMLLoader;
import org.tinylog.Logger;

public class ViewFactory {
    //make it a singleton
    private static ViewFactory viewFactory = new ViewFactory();

    private ViewFactory() {
    }

//    public static ViewFactory getInstance() {
//        return viewFactory;
//    }

    public static FXMLLoader getTicket(){
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource("tickets/agara_qr_ticket.fxml"));
        return fxmlLoader;
    }

    public static FXMLLoader getLogin() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource("login.fxml"));
        Logger.info(fxmlLoader.hashCode());
        return fxmlLoader;
    }

    public static FXMLLoader getTxnProcess() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource("/com/amay/tom/tvm/txn_process.fxml"));
        Logger.info(fxmlLoader.hashCode());
        return fxmlLoader;
    }


    public static FXMLLoader getHome() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource("hello-view.fxml"));
        return fxmlLoader;
    }

    public static Controller getHomeController() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource("hello-view.fxml"));
        return (Controller) fxmlLoader.getController();
    }

    public static FXMLLoader getAnalysis() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource("analysis.fxml"));
        return fxmlLoader;

    }

    public static FXMLLoader getTicketAnalysisDetails() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource
                ("ticket-analysis-details.fxml"));
        return fxmlLoader;

    }

    public static FXMLLoader getAdministration() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource
                ("administration.fxml"));
        return fxmlLoader;

    }

    public static FXMLLoader getPaidFreeTicket() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource
                ("paid-free-ticket.fxml"));
        return fxmlLoader;
    }

    public static FXMLLoader getFreeTicketPrint() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource
                ("free-ticket-print.fxml"));
        return fxmlLoader;
    }

    public static FXMLLoader getPauseEosSelection() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource
                ("administration/pause-eos-selection-view.fxml"));
        return fxmlLoader;
    }

    public static FXMLLoader getResumeShift() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource
                ("administration/resume-shift.fxml"));
        return fxmlLoader;
    }

    public static FXMLLoader getCscOperationsView() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource
                ("cscoperations/csc-operations-view.fxml"));
        return fxmlLoader;
    }

    public static FXMLLoader getTicketDetails() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource
                ("cscoperations/replacement-ticket-details-view.fxml"));
        return fxmlLoader;
    }

    public static FXMLLoader getReplacementDetailsView() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource
                ("qrt-operation/replacement-details-view.fxml"));
        return fxmlLoader;
    }

    public static FXMLLoader getStockManagement() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource
                ("stockmanegment/stocks-add-view.fxml"));
        return fxmlLoader;
    }

    public static FXMLLoader getPreLogin() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource
                ("pre-login-view.fxml"));
        return fxmlLoader;
    }

    public static FXMLLoader getLoginScreen(){
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource
                ("login.fxml"));
        return fxmlLoader;
    }

    public static FXMLLoader getPayment() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource
                ("payment/payment.fxml"));
        return fxmlLoader;
    }

    public static FXMLLoader getQRTOperationsView() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource
                ("qrt-operation/qrt-operations-view.fxml"));
        return fxmlLoader;
    }

    public static FXMLLoader getReplacementView() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource
                ("qrt-operation/replacement-view.fxml"));
        return fxmlLoader;
    }

    public static FXMLLoader getCancelView() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource
                ("qrt-operation/cancel-view.fxml"));
        return fxmlLoader;
    }

    public static FXMLLoader getBottomNav() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource
                ("components/status-bottom-bar-view.fxml"));
        return fxmlLoader;
    }

    public static FXMLLoader getMaintenance() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource
                ("maintenance/Maintenance.fxml"));
        return fxmlLoader;
    }

    public static FXMLLoader getMaintenanceModuleTest() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource
                ("maintenance/ModuleTest.fxml"));
        return fxmlLoader;
    }

    public static FXMLLoader getMaintenanceApplicationControl() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource
                ("maintenance/ApplicationControl.fxml"));
        return fxmlLoader;
    }

    public static FXMLLoader getMaintenanceImportExport() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource
                ("maintenance/ImportExport.fxml"));
        return fxmlLoader;
    }

    public static FXMLLoader getMaintenanceVersion() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource
                ("maintenance/Version.fxml"));
        return fxmlLoader;
    }

    public static FXMLLoader getMaintenanceAlarm() {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewFactory.class.getResource
                ("maintenance/Alarm.fxml"));
        return fxmlLoader;
    }

    public static FXMLLoader getPopupView() {
        return new FXMLLoader(ViewFactory.class.getResource
                ("maintenance/component/status-window.fxml"));
    }

    public static FXMLLoader getConfermationWindow() {
        return new FXMLLoader(ViewFactory.class.getResource
                ("maintenance/component/confermation-window.fxml"));
    }

    public static FXMLLoader getRefundTicketInputView() {
        return new FXMLLoader(ViewFactory.class.getResource("/com/amay/tom/qrt-operation/refund-ticket-input-view.fxml"));
    }

    public static FXMLLoader getRefundTicketDetailsView() {
        return new FXMLLoader(ViewFactory.class.getResource("/com/amay/tom/qrt-operation/refund-ticket-details-view.fxml"));
    }

    public static FXMLLoader getServiceUnavailable() {
        return new FXMLLoader(ViewFactory.class.getResource("/com/amay/tom/view/ServiceUnavailable.fxml"));
    }

    public static FXMLLoader getEOS() {
        return new FXMLLoader(ViewFactory.class.getResource("/com/amay/tom/eos/mp_metro_eos.fxml"));
    }

    public static FXMLLoader getAbnormalStationMode() {
        return new FXMLLoader(ViewFactory.class.getResource("/com/amay/tom/pdu/abnormal_station_mode.fxml"));
    }

    public static FXMLLoader getCollectTicket() {
        return new FXMLLoader(ViewFactory.class.getResource("/com/amay/tom/pdu/collect_ticket_view.fxml"));
    }

    public static FXMLLoader getTicketSummary() {
        return new FXMLLoader(ViewFactory.class.getResource("/com/amay/tom/pdu/ticket_summary.fxml"));
    }

    public static FXMLLoader getPDUWelcomePage() {
        return new FXMLLoader(ViewFactory.class.getResource("/com/amay/tom/pdu/welcome_screen.fxml"));
    }

    public static FXMLLoader getSuccessPage() {
        return new FXMLLoader(ViewFactory.class.getResource("SuccessPage.fxml"));
    }

    public static FXMLLoader getTicketSelectionView() {
        return new FXMLLoader(ViewFactory.class.getResource("/com/amay/tom/tvm/ticket_selection_view.fxml"));
    }

    public static FXMLLoader getTicketSelectionDetailsView() {
        return new FXMLLoader(ViewFactory.class.getResource("/com/amay/tom/tvm/station_selection_view.fxml"));
    }


    public static FXMLLoader getPaymentSummeryView() {
        return new FXMLLoader(ViewFactory.class.getResource("/com/amay/tom/tvm/payment_summery.fxml"));
    }

    public  static FXMLLoader getSessionCompletionView() {
        return new FXMLLoader(ViewFactory.class.getResource("/com/amay/tom/tvm/session_completion_view.fxml"));
    }
    public static FXMLLoader getTVMHomeScreen() {
        return new FXMLLoader(ViewFactory.class.getResource("/com/amay/tom/tvm/main_container.fxml"));
    }

    public static FXMLLoader getSpecialModeScreen() {
        return new FXMLLoader(ViewFactory.class.getResource("/com/amay/tom/tvm/special_mode_view.fxml"));
    }

    public static FXMLLoader getCashPaymentView() {
        return new FXMLLoader(ViewFactory.class.getResource("/com/amay/tom/tvm/cash_payment_view.fxml"));
    }
}

