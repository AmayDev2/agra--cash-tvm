package com.amay.tom.grpc.scugrpc;

import com.amay.tom.grpc.scugrpc.SCUGrpcConnector;
import com.amay.tom.grpc.scugrpc.ScuDataMapper;
import com.amay.tom.model.LastOnline;
import com.amay.tom.model.session.Shift;
import com.amay.tom.repository.adjustment.AdjustedTicketRepository;
import com.amay.tom.repository.session.ShiftRepository;
import com.amay.tom.repository.tickets.TicketsRepository;
import com.amay.tom.utils.env.EnvFile;
import com.amay.tom.utils.files.FileSerializeService;
import io.grpc.StatusRuntimeException;
import org.amaytechnosystems.*;
import org.tinylog.Logger;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

//Transaction service
public class ScuService {

    private final SCUGrpcConnector scuGrpcConnector;
    private TomTransactionServiceGrpc.TomTransactionServiceBlockingStub blockingStub;
    private final String chanelName;

    // Constructor
    public ScuService(SCUGrpcConnector scuGrpcConnector,
                      TomTransactionServiceGrpc.TomTransactionServiceBlockingStub blockingStub, String chanelName) {
        this.scuGrpcConnector = scuGrpcConnector;
        this.blockingStub = blockingStub;
//        this.blockingStub=this.blockingStub.withDeadlineAfter(2, TimeUnit.SECONDS);
        this.chanelName = chanelName;
    }

    // Reconnect to the SCU service
    private void reconnect() {
        scuGrpcConnector.reconnect();
        this.blockingStub = scuGrpcConnector.getBlockingStub();
    }

    // Check if the SCU service is connected, and reconnect if necessary
    private void ensureConnected() {
        if (scuGrpcConnector.isChannelShutdown() || scuGrpcConnector.isChannelTerminated()) {
            this.reconnect();
        }
    }

    // Retry the given task once if it fails due to SCU service unavailability
    private <T> T retryOnce(Supplier<T> task) {
        boolean isSuccessful = false;
        try {
            isSuccessful = true;
            return task.get();  // Attempt the task and return its result
        } catch (StatusRuntimeException e) {
            Logger.warn("SCU service unavailable, retrying...", e.getStatus(), e);
//            new SCUServiceUnavailable("SCU service unavailable", e);

            //TODO: Uncomment the following block if reconnection logic is needed
           /*  if (e.getStatus().getCode().equals(Status.Code.UNAVAILABLE)) {
                 this.reconnect();
             }*/

            try {
                isSuccessful = true;
                return task.get();  // Retry once and return its result if successful
            } catch (StatusRuntimeException retryException) {
                Logger.error("Retry failed: SCU service unavailable", retryException);
                return null;  // Return null to indicate failure after retry
            } finally {
                if (!isSuccessful) {
                    LastOnline lastOnline = (LastOnline) FileSerializeService.retrieveData("lastUpdated.ser", EnvFile.getLastUpdatedFile());
                    FileSerializeService.saveData("lastUpdated.ser",
                            chanelName.equals("SCU") ? lastOnline.setSCU(Instant.now()) : lastOnline.setCCU(Instant.now()),
                            EnvFile.getLastUpdatedFile());
                }
            }
        }
    }

    // Interact with the SCU service

    public void pushTicketIssueInfo(TicketRequestV1 request) {
        Logger.info("Pushed Ticket Data {}", request.getTicketData());
        retryOnce(() -> blockingStub.pushTicketIssueInfo(request));
    }

    public void pushTicketIssueInfo(TicketRequestV1 request, TicketsRepository ticketsRepository) {
        Logger.info("Pushed Ticket Data {}", request.getTicketData());
        retryOnce(() -> {
            TicketResponseV1 ticketResponse = blockingStub.pushTicketIssueInfo(request);
            if (ticketResponse.getResponseMetaData().getErrorCode().equals("200")) {
                ticketsRepository.pushTickets(List.of(request.getTicketData().getTicket().getTicketId()), chanelName);
                Logger.info("Pushed Ticket Data Response {} {} {}", request.getTicketData(), ticketResponse.getResponseMetaData(),chanelName);
                return true;
            } else{
                Logger.error("Pushed Ticket Data Response {} {} {}", request.getTicketData(), ticketResponse.getResponseMetaData(),chanelName);
                return false;
            }
        });
    }

    public TicketResponseV1 pushTicketIssueInfoBulk(TicketRequestV1 request) throws Exception {
        return blockingStub.pushTicketIssueInfo(request);

    }


    public String getVersion(VersionRequestV1 request) {
        VersionResponseV1 response = retryOnce(() -> blockingStub.getVersion(request));
        return Optional.ofNullable(response)
                .map(VersionResponseV1::getVersion)
                .orElse(null);
    }

    public void pushShiftInfo(Shift shift) {
        retryOnce(() -> blockingStub.updateShiftInfo(ScuDataMapper.createShiftRequest(shift)));
    }

    public ShiftResponseV1 pushShiftInfoBulk(Shift shift) throws Exception {
        return blockingStub.updateShiftInfo(ScuDataMapper.createShiftPushRequest(shift));
    }

    public void pushShiftEnd(Shift shift) {
        retryOnce(() -> blockingStub.updateShiftInfo(ScuDataMapper.createShiftEndRequest(shift)));
    }

    public void pushShiftEndAsync(Shift shift, ShiftRepository shiftRepository) {
        CompletableFuture
                .supplyAsync(() -> blockingStub.updateShiftInfo(
                        ScuDataMapper.createShiftEndRequest(shift)))
                .thenAccept(response -> {
                    Logger.info("Shift End Request Sent: {}", response);
                    if (response.getResponseMetaData().getErrorCode().equals("200")) {
                        shiftRepository.pushShifts(List.of(shift.getShiftId()), chanelName);
                    }
                })
                .exceptionally(ex -> {
                    Logger.error("Failed to send Shift End Request", ex);
                    return null;
                });

    }


    public void pushShiftPause(Shift shift) {
        retryOnce(() -> blockingStub.updateShiftInfo(ScuDataMapper.createShiftPauseRequest(shift)));
    }

    public void resumeShiftPause(Shift shift) {
        retryOnce(() -> blockingStub.updateShiftInfo(ScuDataMapper.createShiftResumeRequest(shift)));
    }

    public void pushTicketAdjustedInfo(TicketAdjustedRequestV1 ticketRequestV1, AdjustedTicketRepository adjustedTicketRepository) {
        retryOnce(() -> {
            TicketAdjustedResponseV1 adjustedResponseV1 = blockingStub.pushTicketAdjustedData(ticketRequestV1);
            if (adjustedResponseV1.getResponseMetaData().getErrorCode().equals("200")) {
                adjustedTicketRepository.pushTickets(List.of(ticketRequestV1.getTicketData().getAdjustedTicket().getAdjustId()), chanelName);
            }
            return adjustedResponseV1;
        });
    }

    public void pushTotalStock(TomStockRequestV1 stockRequest) {
        retryOnce(() -> blockingStub.pushStocksData(stockRequest));
    }

    public TicketRefundResponseV1 getTicketByNumber(TicketRequestV1 request) {
        return blockingStub.getTicketByNumber(request);
    }

    public TicketRefundResponseV2 refundTicket(TicketRefundRequestV1 ticketRefundRequest) {
        return blockingStub.refundByNumber(ticketRefundRequest);
    }

    public TicketAnalysisResponseV1 getTicketAnalysisByNumber(TicketRequestV1 ticketRefundRequest) {
        return blockingStub.getTicketAnalysisDataByNumber(ticketRefundRequest);
    }

    public TicketAdjustedResponseV1 pushTicketAdjustedData(TicketAdjustedRequestV1 ticketAdjustedRequestV1) {
        return blockingStub.pushTicketAdjustedData(ticketAdjustedRequestV1);
    }


    public String getTodayLastShiftId() {
        return getLastShift(ScuDataMapper.createLastShiftRequest()).getShiftData().getShift().getShiftId();
//        if(lastShiftResponse.getResponseMetaData().getErrorCode().equals("200"))
//            return lastShiftResponse.getShiftData().getShift().getShiftId();
//        else if(lastShiftResponse.getResponseMetaData().getErrorCode().equals("755")){
//            return lastShiftResponse.getShiftData().getShift().getShiftId()+1;
//        }
    }

    private LastShiftResponseV1 getLastShift(LastShiftRequestV1 lastShiftRequestV1) {
        return blockingStub.getLastShift(lastShiftRequestV1);
    }
}
