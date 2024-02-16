package com.YipYapTimeAPI.YipYapTimeAPI.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {

    @Value("${example.rabbitmq.privateQueue}")
    private String privateQueue;

    @Value("${example.rabbitmq.groupQueue}")
    private String groupQueue;

    @Value("${example.rabbitmq.exchange}")
    private String exchange;

    @Value("${example.rabbitmq.privateRoutingKey}")
    private String privateRoutingKey;

    @Value("${example.rabbitmq.groupRoutingKey}")
    private String groupRoutingKey;

    @Bean
    public Queue privateQueue() {
        return new Queue(privateQueue, true);
    }

    @Bean
    public Queue groupQueue() {
        return new Queue(groupQueue, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding groupBinding(Queue groupQueue, TopicExchange exchange) {
        return BindingBuilder.bind(groupQueue).to(exchange).with(groupRoutingKey);
    }

    @Bean
    public Binding privateBinding(Queue privateQueue, TopicExchange exchange) {
        return BindingBuilder.bind(privateQueue).to(exchange).with(privateRoutingKey);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        rabbitTemplate.setExchange(exchange);
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
