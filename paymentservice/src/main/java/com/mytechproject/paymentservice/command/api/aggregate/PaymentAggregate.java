package com.mytechproject.paymentservice.command.api.aggregate;

import com.mytechproject.commonservice.commands.CancelPaymentCommand;
import com.mytechproject.commonservice.commands.ValidatePaymentCommand;
import com.mytechproject.commonservice.events.PaymentCancelledEvent;
import com.mytechproject.commonservice.events.PaymentProcessedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
@Slf4j
public class PaymentAggregate {

    @AggregateIdentifier
    private String paymentId;
    private String orderId;
    private String paymentStatus;

    public PaymentAggregate () {

    }

    @CommandHandler
    public PaymentAggregate(ValidatePaymentCommand validatePaymentCommand) {
        // Validate the Payment Details and Publish Payment Process Event
        log.info("Executing Validate Payment Command for " +
                "Order Id : {} and Payment Id : {}", validatePaymentCommand.getOrderId(),
                validatePaymentCommand.getPaymentId());

        PaymentProcessedEvent event = new
                PaymentProcessedEvent(validatePaymentCommand.getPaymentId(), validatePaymentCommand.getOrderId());

        AggregateLifecycle.apply(event);
        log.info("PaymentProcessedEvent applied");

    }

    @CommandHandler
    public void hanndle(CancelPaymentCommand cancelPaymentCommand) {

        PaymentCancelledEvent event = new PaymentCancelledEvent();
        BeanUtils.copyProperties(cancelPaymentCommand,event);
        AggregateLifecycle.apply(event);

    }

    @EventSourcingHandler
    public void on(PaymentProcessedEvent event) {
        this.paymentId = event.getPaymentId();
        this.orderId = event.getOrderId();
    }

    @EventSourcingHandler
    public void on(PaymentCancelledEvent event) {
        this.paymentId = event.getPaymentId();
        this.orderId = event.getOrderId();
    }

}
