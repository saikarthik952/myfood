package com.food.saga.myfood.controller;

import com.food.saga.myfood.config.WorkFlowClientConfig;
import com.food.saga.myfood.temporal.workflows.FoodDeliveryWorkFlow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/")
public class TestController {

    @Autowired
    WorkflowClient workflowClient;


    @GetMapping(path = "order", produces = "text/plain")
    public String orderFood() {
        WorkflowOptions options = WorkflowOptions.newBuilder().setTaskQueue("food_order").build();
        FoodDeliveryWorkFlow order = workflowClient.newWorkflowStub(FoodDeliveryWorkFlow.class, options);
        return order.orderFood("Pizza", "$30");
    }
}
