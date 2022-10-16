package pro.sky.telegrambot.sender;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

import static java.time.format.DateTimeFormatter.ofPattern;

@Service
public class TelegramBotNotificationsSender {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    private final NotificationsRepository notificationsRepository;

    DateTimeFormatter dateFormatter = ofPattern("d MMMM uuuu", Locale.getDefault());
    DateTimeFormatter timeFormatter = ofPattern("HH:mm", Locale.getDefault());

    public TelegramBotNotificationsSender(TelegramBot telegramBot, NotificationsRepository notificationsRepository) {
        this.telegramBot = telegramBot;
        this.notificationsRepository = notificationsRepository;
    }


    @Scheduled(cron = "0 0/1 * * * *")
    public void notificationSender(){

        LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> notificationTasks = notificationsRepository.findAllByDateTime(dateTime);
        for (NotificationTask n : notificationTasks) {
            String messageText = "[" + dateTime.truncatedTo(ChronoUnit.MINUTES).format(dateFormatter)
                    + ", " + dateTime.truncatedTo(ChronoUnit.MINUTES).format(timeFormatter) + "]"
                    + " Напоминание: " + n.getNotification();
            SendMessage message = new SendMessage(n.getChatId(), messageText);
            SendResponse response = telegramBot.execute(message);
            if (!response.isOk()) {
                logger.error("Response error: {} {}", response.errorCode(), response.message());
            }
        }
    }

}
