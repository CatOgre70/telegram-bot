package pro.sky.telegrambot.sender;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.service.NotificationsService;

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

    private final NotificationsService notificationsService;

    DateTimeFormatter dateFormatter = ofPattern("d MMMM uuuu", Locale.getDefault());
    DateTimeFormatter timeFormatter = ofPattern("HH:mm", Locale.getDefault());

    public TelegramBotNotificationsSender(TelegramBot telegramBot, NotificationsService notificationsService) {
        this.telegramBot = telegramBot;
        this.notificationsService = notificationsService;
    }


    @Scheduled(cron = "0 0/1 * * * *")
    public void notificationSender(){

        // Sending current messages
        LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> notificationTasks = notificationsService.findAllByDateTime(dateTime);
        for (int i = 0; i < notificationTasks.size(); i++) {
            NotificationTask n = notificationTasks.get(i);
            String messageText = "[" + dateTime.truncatedTo(ChronoUnit.MINUTES).format(dateFormatter)
                    + ", " + dateTime.truncatedTo(ChronoUnit.MINUTES).format(timeFormatter) + "]"
                    + " Напоминание: " + n.getNotification();

            SendResponse response = SendMessageToTelegram(n.getChatId(), messageText);
            if (!response.isOk()) {
                logger.error("Response error: {} {}", response.errorCode(), response.message());
                continue;
            }
            n.setSent(true);
            notificationsService.save(n);
        }

        // Sending outdated messages
        notificationTasks = notificationsService.findNotificationTaskByDateTimeBeforeAndSent(dateTime, false);

        for (int i = 0; i < notificationTasks.size(); i++) {
            NotificationTask n = notificationTasks.get(i);
            String messageText = "[" + dateTime.truncatedTo(ChronoUnit.MINUTES).format(dateFormatter)
                    + ", " + dateTime.truncatedTo(ChronoUnit.MINUTES).format(timeFormatter) + "]"
                    + " Пропущенное напоминание: " + n.getNotification();
            SendResponse response = SendMessageToTelegram(n.getChatId(), messageText);
            if (!response.isOk()) {
                logger.error("Response error: {} {}", response.errorCode(), response.message());
                continue;
            }
            n.setSent(true);
            notificationsService.save(n);
        }

    }

    public SendResponse SendMessageToTelegram(Long chatId, String textMessage){
        SendMessage message = new SendMessage(chatId, textMessage);
        SendResponse response = telegramBot.execute(message);
        return response;
    }

}
