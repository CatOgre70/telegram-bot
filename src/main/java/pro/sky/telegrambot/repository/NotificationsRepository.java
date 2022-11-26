package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationsRepository extends JpaRepository<NotificationTask, Long> {

    boolean existsByChatIdAndNotificationAndDateTime(Long chatId, String notification, LocalDateTime dateTime);
    List<NotificationTask> findAllByDateTime(LocalDateTime dateTime);
    List<NotificationTask> findNotificationTaskByDateTimeBeforeAndSent(LocalDateTime dateTime, boolean sent);

}
