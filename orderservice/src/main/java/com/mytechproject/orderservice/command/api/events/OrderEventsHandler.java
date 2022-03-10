package com.mytechproject.orderservice.command.api.events;

import com.mytechproject.commonservice.events.OrderCancelledEvent;
import com.mytechproject.commonservice.events.OrderCompletedEvent;
import com.mytechproject.orderservice.command.api.data.Order;
import com.mytechproject.orderservice.command.api.data.OrderRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class OrderEventsHandler {

    private OrderRepository orderRepository;

    public OrderEventsHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @EventHandler
    public void on(OrderCreatedEvent orderCreatedEvent) {

        Order order = new Order();
       // BeanUtils.copyProperties(order, orderCreatedEvent);
        order.setOrderId(orderCreatedEvent.getOrderId());
        order.setAddressId(orderCreatedEvent.getAddressId());
        order.setProductId(orderCreatedEvent.getProductId());
        order.setUserId(orderCreatedEvent.getUserId());
        order.setOrderStatus(orderCreatedEvent.getOrderStatus());
        orderRepository.save(order);
    }

    @EventHandler
    public void on(OrderCompletedEvent event) {

        Order order = orderRepository.findById(event.getOrderId()).get();
        order.setOrderStatus(event.getOrderStatus());
        orderRepository.save(order);
    }

    @EventHandler
    public void on(OrderCancelledEvent event) {
        Order order = orderRepository.findById(event.getOrderId()).get();
        order.setOrderStatus(event.getOrderStatus());
        orderRepository.save(order);
    }
}
