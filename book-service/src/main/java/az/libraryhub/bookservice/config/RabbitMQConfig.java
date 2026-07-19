package az.libraryhub.bookservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "library.events";
    public static final String BORROW_QUEUE = "borrow.events.queue";
    public static final String BORROW_ROUTING_KEY = "borrow.event";

    @Bean
    public DirectExchange libraryExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue borrowQueue() {
        return new Queue(BORROW_QUEUE, true);
    }

    @Bean
    public Binding borrowBinding(Queue borrowQueue, DirectExchange libraryExchange) {
        return BindingBuilder.bind(borrowQueue)
                .to(libraryExchange)
                .with(BORROW_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}