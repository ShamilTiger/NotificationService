package service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.mail.javamail.JavaMailSender;
import ru.ilyin.dto.UserEvent;
import ru.ilyin.service.NotificationService;


@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = "user-events")
class NotificationService`IntegrationTest {

    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Autowired
    private NotificationService notificationService;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    void shouldSendEmailOnUserCreatedEvent() throws Exception {

    }
}