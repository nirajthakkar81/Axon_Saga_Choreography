package com.mytechproject.userservice.projection;

import com.mytechproject.commonservice.model.CardDetails;
import com.mytechproject.commonservice.model.User;
import com.mytechproject.commonservice.queries.GetUserPaymentDetailsQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class UserProjection {

    @QueryHandler
    public User getUserPaymentDetails (GetUserPaymentDetailsQuery query) {
        CardDetails cardDetails = CardDetails.builder()
                .name("Mitchel Johnson")
                .validUntilMonth(10)
                .validUntilYear(2024)
                .cardNumber("123456789")
                .cvv(123)
                .build();

        return User.builder()
                .userId(query.getUserId())
                .firstName("Mitchel")
                .lastName("Johnson")
                .cardDetails(cardDetails)
                .build();

    }
}
