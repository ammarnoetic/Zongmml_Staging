//package com.noetic.zong_billing.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.noetic.zong_billing.entities.UcipChargingEntity;
//import com.noetic.zong_billing.repositories.UcipChargingRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//@Component
//@Service
//public class RequestConsumer {
//
//    Logger log = LoggerFactory.getLogger(RequestConsumer.class.getName());
//
//    @Autowired
//    private ApplicationEventPublisher applicationEventPublisher;
//    @Autowired
//    private UcipChargingRepository ucipChargingRepository;
//    ObjectMapper objectMapper = new ObjectMapper();
//
//    @RabbitListener(queues = {"zongrequestQueue"})
//    public void receive(String msg) {
//
//        try {
//            log.info("Queue Listner"+msg);
//
//            UcipChargingEntity customMessage = objectMapper.readValue(msg, UcipChargingEntity.class);
//            ucipChargingRepository.save(customMessage);
//            log.info("Custom Message "+customMessage.getMsisdn());
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
