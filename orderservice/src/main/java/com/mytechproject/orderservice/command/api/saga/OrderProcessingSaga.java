package com.mytechproject.orderservice.command.api.saga;

import com.mytechproject.commonservice.commands.*;
import com.mytechproject.commonservice.events.*;
import com.mytechproject.commonservice.model.User;
import com.mytechproject.commonservice.queries.GetUserPaymentDetailsQuery;
import com.mytechproject.orderservice.command.api.events.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.UUID;

/**
 * This is an Orchestration Based Saga Class
 */

@Saga
@Slf4j
public class OrderProcessingSaga {

    // There is a need to make this variables transient.
    @Autowired
    private transient CommandGateway commandGateway;
    @Autowired
    private transient QueryGateway queryGateway;


    public OrderProcessingSaga() {
      // This default constructor is mandatory to prevent SagaCreateException
    }

    @StartSaga
    @SagaEventHandler(associationProperty =  "orderId")
    public void on(OrderCreatedEvent event) {
        log.info("OrderCreatedEvent in Saga for Order Id : {}", event.getOrderId());
        User user = null;
        try {

            GetUserPaymentDetailsQuery getUserPaymentDetailsQuery =
                    new GetUserPaymentDetailsQuery(event.getUserId());
            user = queryGateway.query(getUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();

        } catch(Exception e) {
            log.error(e.getMessage());
            // Start the compensating transaction
            cancelOrderCommand(event.getOrderId());
        }

        ValidatePaymentCommand validatePaymentCommand = ValidatePaymentCommand.builder()
                .cardDetails(user.getCardDetails())
                .orderId(event.getOrderId())
                .paymentId(UUID.randomUUID().toString())
                .build();

        commandGateway.sendAndWait(validatePaymentCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent event) {

        log.info("PaymentProcessedEvent in Saga for Order Id : {}", event.getOrderId());
        try {

            /*  Uncomment this code if you want to Simulate failure of Shipment and Cancel Order
            if(true) {
                throw new Exception("Simulating Shipment Failed");
            } */

            ShipOrderCommand shipOrderCommand =
                    ShipOrderCommand.builder()
                            .shipmentId(UUID.randomUUID().toString())
                            .orderId(event.getOrderId())
                            .build();

            commandGateway.sendAndWait(shipOrderCommand);
        } catch(Exception e) {
            log.error(e.getMessage());
            // Start the compensating transaction
            cancelPaymentCommand(event);
        }

    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderShippedEvent event) {

        log.info("OrderShippedEvent in Saga for Order Id : {}", event.getOrderId());
        try {
            CompleteOrderCommand completeOrderCommand =
                    CompleteOrderCommand.builder()
                            .orderId(event.getOrderId())
                            .orderStatus("APPROVED")
                            .build();

            commandGateway.sendAndWait(completeOrderCommand);
        } catch(Exception e) {
            log.error(e.getMessage());
            // Start the compensating transaction
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    @EndSaga
    public void handle(OrderCompletedEvent event) {
        log.info("OrderCompletedEvent in Saga for Order Id : {}", event.getOrderId());
    }
    @SagaEventHandler(associationProperty = "orderId")
    @EndSaga
    public void handle(OrderCancelledEvent event) {
        log.info("OrderCancelledEvent in Saga for Order Id : {}", event.getOrderId());
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentCancelledEvent event) {
        log.info("PaymentCancelledEvent in Saga for Order Id : {}", event.getOrderId());
        cancelOrderCommand(event.getOrderId());
    }

    private void cancelOrderCommand(String orderId) {
        CancelOrderCommand cancelOrderCommand = new CancelOrderCommand(orderId);
        commandGateway.sendAndWait(cancelOrderCommand);
    }

    private void cancelPaymentCommand(PaymentProcessedEvent event) {

        CancelPaymentCommand cancelPaymentCommand = new
                CancelPaymentCommand(event.getPaymentId(), event.getOrderId());
        commandGateway.sendAndWait(cancelPaymentCommand);
    }

}
