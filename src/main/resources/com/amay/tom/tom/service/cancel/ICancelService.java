package com.amay.tom.service.cancel;

import com.amay.tom.model.tickets.TicketsDto;
import javafx.collections.ObservableList;

public interface ICancelService {
    public boolean markTicketCancelByOrderId(String orderId);
    public void getCancelTicketInfo(String orderId);

    ObservableList<TicketsDto> getObserverList();
}
