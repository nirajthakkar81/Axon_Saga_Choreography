# Axon_Saga_Choreography

Download the Axon Server jar from the repository and launch the Axon server using
java -jar axonserver-4.5.10.jar

Verify the Axon Server console on browser using http://localhost:8024


Import all the maven modules from the repository and import it in IntelliJ or your favourite Editor

Make sure to install JDK 11 on your local machine

Launch orderservice, payment service, shipment service, user service using their main class


Verify if all the services are launched.


In order to create an order, launch the order service on Postman with POST method and request body

http://localhost:9091/orders 

{
    "productId" : "3z7a3-8091-4b9-99fd-1180864983d",
    "userId" : "test-user",
    "addressId" : "3h7ue-2bc2-4e1-b319-b8dd99c3868",
   "quantity" : "1"

}

Response Received will be "Order Created"

Check the respective tables of each Services and Log files of each service to see if the Order is Completed.

