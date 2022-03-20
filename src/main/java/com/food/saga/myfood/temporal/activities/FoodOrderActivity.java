package com.food.saga.myfood.temporal.activities;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface FoodOrderActivity {


    public boolean makePayment(String amount, String orderId);

    public boolean acknowledgeCustomer(String orderId);

    public boolean notifyRestuarantAndFetchAck(String food, String orderId, String restaurantId);

    public boolean sendPaymentToRestaurant(String amount, String orderId, String restaurantId);

    public String searchAndAssignDeliveryPartner(String orderId, String restaurantId);

    public boolean revert(String amount, String orderId);

    public boolean acknowledgeCustomerForFailure(String orderId);

    public boolean notifyRestuarantAndSendFailAck(String food, String orderId, String restaurantId);

    public boolean revertPaymentToRestaurant(String amount, String orderId, String restaurantId);

    public String noDeliveryPersonsFound(String orderId, String restaurantId);


}
