//package com.noetic.zong_billing.utils;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class RequestSender {
//
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//    private String exchange = "direct-exchange";
//    Logger log = LoggerFactory.getLogger(RequestSender.class.getName());
//    public void send(String routingKey, Object message) {
//        try {
//            log.info("Sending Messages To QUEUE"+routingKey);
//            rabbitTemplate.convertAndSend(exchange, routingKey, message);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//}
