package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationsService {

    private final NotificationsRepository notificationsRepository;

    public NotificationsService(NotificationsRepository notificationsRepository) {
        this.notificationsRepository = notificationsRepository;
    }

    public boolean existsByChatIdAndNotificationAndDateTime(Long chatId, String notification, LocalDateTime dateTime){
        return notificationsRepository.existsByChatIdAndNotificationAndDateTime(chatId, notification, dateTime);
    }

    public List<NotificationTask> findAllByDateTime(LocalDateTime dateTime){
        return notificationsRepository.findAllByDateTime(dateTime);
    }

    public void save(NotificationTask notificationTask) {
        notificationsRepository.save(notificationTask);
    }
}
