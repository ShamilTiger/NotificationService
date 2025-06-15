package service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import ru.ilyin.ApplicationRunner;
import ru.ilyin.dto.EmailRequest;
import ru.ilyin.dto.EventType;
import ru.ilyin.dto.UserEvent;
import ru.ilyin.service.NotificationService;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;


@SpringBootTest(classes = ApplicationRunner.class)
@EmbeddedKafka(partitions = 1, topics = "user-events")
class NotificationServiceIntegrationTest {

    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Autowired
    private NotificationService notificationService;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    void shouldSendEmailOnUserCreatedEvent() throws Exception {
        UserEvent event = new UserEvent(EventType.CREATED, "test@mail.ru");
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        kafkaTemplate.send("user-events", event);
        Thread.sleep(1000);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertNotNull(message.getTo(), "Поле 'to' не должно быть null");
        assertEquals("test@mail.ru", message.getTo()[0]);
        assertEquals("Аккаунт создан", message.getSubject());
    }

    @Test
    void shouldSendEmailViaApi(){
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        EmailRequest request = new EmailRequest();
        request.setTo("test@mail.ru");
        request.setSubject("Test Subject");
        request.setText("Test Text");

        notificationService.sendEmail(request.getTo(),request.getSubject(),request.getText());

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertNotNull(message.getTo(), "Поле 'to' не должно быть null");
        assertEquals("test@mail.ru", message.getTo()[0]);
        assertEquals("Test Subject", message.getSubject());
        assertEquals("Test Text", message.getText());
    }
}