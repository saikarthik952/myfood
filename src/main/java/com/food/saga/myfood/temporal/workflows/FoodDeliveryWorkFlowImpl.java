package com.food.saga.myfood.temporal.workflows;

import com.food.saga.myfood.temporal.activities.FoodOrderActivity;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Objects;

@Slf4j
public class FoodDeliveryWorkFlowImpl implements FoodDeliveryWorkFlow{

    private final ActivityOptions options =
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofHours(1))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(1).build())
                    .build();
    private final FoodOrderActivity foodOrderActivity = Workflow.newActivityStub(FoodOrderActivity.class, options);

    @Override
    public String orderFood(String food, String amount) {
        final Saga saga = new Saga(new Saga.Options.Builder().setParallelCompensation(false).build());
        try {
            final String orderId = Workflow.randomUUID().toString();

            final boolean isPaymentDone = foodOrderActivity.makePayment(amount, orderId);
            saga.addCompensation(() -> foodOrderActivity.revert(amount, orderId));
            if (!isPaymentDone) {
                throw new RuntimeException("Payment Failed");
            }

            final boolean isCustomerNotified = foodOrderActivity.acknowledgeCustomer(orderId);
            saga.addCompensation(() -> foodOrderActivity.acknowledgeCustomerForFailure(orderId));


            if (!isCustomerNotified) {
                throw new RuntimeException("Order creation Failed");
            }

            final boolean isRestaurantAcknowledged = foodOrderActivity.notifyRestuarantAndFetchAck(food, orderId, "1");
            saga.addCompensation(() -> foodOrderActivity.notifyRestuarantAndSendFailAck(food, orderId, "1"));

            if (!isRestaurantAcknowledged) {
                throw new RuntimeException("Ack Failed");
            }

            final boolean amountSentToRes = foodOrderActivity.sendPaymentToRestaurant(amount, orderId, "1");
            saga.addCompensation(() -> foodOrderActivity.revertPaymentToRestaurant(amount, orderId, "1"));

            if (!amountSentToRes) {
                throw new RuntimeException("Payment to Res Failed");
            }

            final String deliveryPartnerAssigned = foodOrderActivity.searchAndAssignDeliveryPartner(orderId, "1");
            saga.addCompensation(() -> foodOrderActivity.noDeliveryPersonsFound(orderId, "1"));

            if (Objects.isNull(deliveryPartnerAssigned)) {
                throw new RuntimeException("No Delivery Partner Found");
            }

            return orderId;
        }
        catch (final RuntimeException activityFailure) {
            log.error(activityFailure.toString());
            saga.compensate();
            return "FAILED";
        }
    }
}
