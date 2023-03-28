package com.itender.rabbitmq.direct.config;

import com.sun.org.apache.regexp.internal.RE;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author itender
 * @date 2022/9/5 11:05
 * @desc
 */
@Configuration
public class DirectConfig {

    @Bean(name = "directExchange")
    public DirectExchange getDirectExchange() {
        return new DirectExchange("directExchange",true, false);
    }

    @Bean(name = "directQueue")
    public Queue getDirectQueue() {
        return new Queue("directQueue", true, false, false);
    }

    @Bean(name = "directBinding")
    public Binding getDirectBinding(@Qualifier(value = "directExchange") DirectExchange directExchange,
                                    @Qualifier(value = "directQueue") Queue directQueue) {
        return BindingBuilder.bind(directQueue).to(directExchange).with("directRoutingKey");
    }
}
