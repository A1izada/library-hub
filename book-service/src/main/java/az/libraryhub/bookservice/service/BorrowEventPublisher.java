package az.libraryhub.bookservice.service;

import az.libraryhub.bookservice.config.RabbitMQConfig;
import az.libraryhub.bookservice.dto.BorrowEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BorrowEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public BorrowEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishBorrowEvent(Long userId, Long bookId, String bookTitle) {
        BorrowEvent event = new BorrowEvent("BORROWED", userId, bookId, bookTitle, LocalDateTime.now());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.BORROW_ROUTING_KEY, event);
    }

    public void publishReturnEvent(Long userId, Long bookId, String bookTitle) {
        BorrowEvent event = new BorrowEvent("RETURNED", userId, bookId, bookTitle, LocalDateTime.now());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.BORROW_ROUTING_KEY, event);
    }
}
