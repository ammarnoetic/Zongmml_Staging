//
//package com.noetic.zong_billing.utils;
//
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.DirectExchange;
//import org.springframework.amqp.core.Queue;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RabbitMQConfig {
//
//    @Bean
//    Queue retryQueue() {
//        return new Queue("zongrequestQueue", false);
//    }
//
//    @Bean
//    DirectExchange exchange() {
//        return new DirectExchange("direct-exchange");
//    }
//
//    @Bean
//    Binding retryBinding(Queue retryQueue, DirectExchange exchange) {
//        return BindingBuilder.bind(retryQueue).to(exchange).with("zongrequestQueue");
//    }
//
//}