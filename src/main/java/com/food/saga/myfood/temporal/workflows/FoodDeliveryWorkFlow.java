package com.food.saga.myfood.temporal.workflows;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface FoodDeliveryWorkFlow {

    @WorkflowMethod
    public String orderFood(String food, String amount);
}
