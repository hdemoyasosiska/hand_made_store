package com.example.hm_store_for_rbd;

import com.example.hm_store.Services.EmailService;
import jakarta.mail.Message;
import jakarta.mail.Transport;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Test
    void sendEmail_shouldSendEmailSuccessfully() {
        // Мокируем статический метод Transport.send()
        try (MockedStatic<Transport> mockedTransport = Mockito.mockStatic(Transport.class)) {
            // Настраиваем поведение метода send()
            mockedTransport.when(() -> Transport.send(any(Message.class))).thenAnswer(invocation -> null);

            // Создаем экземпляр тестируемого класса
            EmailService emailService = new EmailService();

            // Вызываем метод sendEmail()
            emailService.sendEmail("Test message");

            // Проверяем, что Transport.send() был вызван
            mockedTransport.verify(() -> Transport.send(any(Message.class)), times(1));
        }
    }
}
