package com.food.saga.myfood.config;

import com.food.saga.myfood.temporal.activities.FoodOrderActivity;
import com.food.saga.myfood.temporal.activities.FoodOrderActivityImpl;
import com.food.saga.myfood.temporal.workflows.FoodDeliveryWorkFlowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkFlowClientConfig {

    @Bean
    public WorkflowClient buildClient() {
        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
        WorkflowClient client = WorkflowClient.newInstance(service);
        WorkerFactory factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker("food_order");
        worker.registerWorkflowImplementationTypes(FoodDeliveryWorkFlowImpl.class);
        FoodOrderActivity foodOrderActivity = new FoodOrderActivityImpl();
        worker.registerActivitiesImplementations(foodOrderActivity);
        factory.start();
        return client;
    }
}
