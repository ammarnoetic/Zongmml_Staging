package com.noetic.zong_billing.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Component
public class AppBootListner implements ApplicationListener<ApplicationReadyEvent> {


    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

        try {
            System.out.println("New Code is Running");
            HttpPoolingManager.getPoolingManagerForChargingService();
            HttpPoolingManager.getPoolingManagerForPostPaidService();
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        }
    }
}
