package com.amay.tom.model.tickets;

import com.amay.tom.model.payment.PaymentResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
@Data
public class PreGeneratadTicket {
    private PaymentResponse paymentResponse;
    private ProperTicketOrder properTicketOrder;
}
