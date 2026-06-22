package com.example.coupondemo.config;

import com.example.coupondemo.mq.CouponProducer;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue couponQueue() {
        return new Queue(CouponProducer.QUEUE, true);
    }

    @Bean
    public DirectExchange couponExchange() {
        return new DirectExchange(CouponProducer.EXCHANGE, true, false);
    }

    @Bean
    public Binding couponBinding() {
        return BindingBuilder.bind(couponQueue())
                .to(couponExchange())
                .with(CouponProducer.ROUTING_KEY);
    }
}
