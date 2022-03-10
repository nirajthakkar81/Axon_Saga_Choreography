package com.mytechproject.shipmentservice.command.api.events;

import com.mytechproject.commonservice.events.OrderShippedEvent;
import com.mytechproject.shipmentservice.command.api.data.Shipment;
import com.mytechproject.shipmentservice.command.api.data.ShipmentRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ShipmentEventsHandler {

    private ShipmentRepository shipmentRepository;

    public ShipmentEventsHandler(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    @EventHandler
    public void on(OrderShippedEvent event) {

        Shipment shipment = new Shipment();
        BeanUtils.copyProperties(event, shipment);
        shipmentRepository.save(shipment);
    }


}
