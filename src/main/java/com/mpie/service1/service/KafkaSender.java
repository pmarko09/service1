package com.mpie.service1.service;

import com.mpie.service1.model.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaSender {

    @Value("${service1.rented-books.topic:rented-books}")
    private String rentedBooksTopic;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendBookRented(Book book) {
        log.info("Send notification to topic about rented book: {}, with data: {}", rentedBooksTopic, book);
        kafkaTemplate.send(rentedBooksTopic, book);
    }

    public void sendBookReturned(Book book) {
        log.info("Send notification to topic about returned book: {}, with data: {}", rentedBooksTopic, book);
        kafkaTemplate.send(rentedBooksTopic, book);
    }
}