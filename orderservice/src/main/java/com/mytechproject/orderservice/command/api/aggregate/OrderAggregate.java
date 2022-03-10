package com.mytechproject.orderservice.command.api.aggregate;

import com.mytechproject.commonservice.commands.CancelOrderCommand;
import com.mytechproject.commonservice.commands.CompleteOrderCommand;
import com.mytechproject.commonservice.events.OrderCancelledEvent;
import com.mytechproject.commonservice.events.OrderCompletedEvent;
import com.mytechproject.orderservice.command.api.command.CreateOrderCommand;
import com.mytechproject.orderservice.command.api.events.OrderCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private String productId;
    private String userId;
    private String addressId;
    private Integer quantity;
    private String orderStatus;

    public OrderAggregate() {

    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand createOrderCommand) {

        // Validate the command

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);
        AggregateLifecycle.apply(orderCreatedEvent);

    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent orderCreatedEvent) {

        this.orderStatus = orderCreatedEvent.getOrderStatus();
        this.orderId = orderCreatedEvent.getOrderId();
        this.productId = orderCreatedEvent.getProductId();
        this.userId = orderCreatedEvent.getUserId();
        this.addressId = orderCreatedEvent.getAddressId();
        this.quantity = orderCreatedEvent.getQuantity();

    }

    @CommandHandler
    public void handle(CompleteOrderCommand completeOrderCommand) {

        // Validate the Command
        // Publish Order Completed Event
        OrderCompletedEvent orderCompletedEvent =
                OrderCompletedEvent.builder()
                        .orderId(completeOrderCommand.getOrderId())
                        .orderStatus(completeOrderCommand.getOrderStatus())
                        .build();

        AggregateLifecycle.apply(orderCompletedEvent);

    }

    @CommandHandler
    public void handle(CancelOrderCommand command) {

        OrderCancelledEvent event = new OrderCancelledEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);

    }

    @EventSourcingHandler
    public void on(OrderCompletedEvent event) {

        this.orderStatus = event.getOrderStatus();
    }

    @EventSourcingHandler
    public void on(OrderCancelledEvent event) {
        this.orderStatus = event.getOrderStatus();
    }

}
