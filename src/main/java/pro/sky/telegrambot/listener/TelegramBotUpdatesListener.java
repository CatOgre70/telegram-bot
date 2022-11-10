package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exceptions.AnswerNotFoundException;
import pro.sky.telegrambot.model.Answer;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.service.AnswersService;
import pro.sky.telegrambot.service.NotificationsService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.format.DateTimeFormatter.*;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;
    private final AnswersService answersService;

    private final NotificationsService notificationsService;

    private List<Answer> answersDb;
    private String helpMessage;

    private static final DateTimeFormatter dateFormatter = ofPattern("d MMMM uuuu", Locale.getDefault());
    private static final DateTimeFormatter timeFormatter = ofPattern("HH:mm", Locale.getDefault());
    private static final Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");

    private static final DateTimeFormatter dateTimeFormatterForParsing = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");


    public TelegramBotUpdatesListener(TelegramBot telegramBot, AnswersService answersService, NotificationsService notificationsService) {
        this.telegramBot = telegramBot;
        this.answersService = answersService;
        this.notificationsService = notificationsService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
        answersDb = answersService.getAllAnswers();
        boolean isFound = false;
        for (Answer a : answersDb) {
            if(a.getQuestion().equals("/help")){
                isFound = true;
                helpMessage = a.getAnswer();
            }
        }
        if(!isFound){
            String errorMessage = "Critical error: /help record was not found in the database!";
            logger.error(errorMessage);
            throw new AnswerNotFoundException(errorMessage);
        }
    }

    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {
            logger.info("Processing update: {}", update);

            // Process your updates here

            if (update.message() != null) {
                String inboundMessage = update.message().text();
                if (inboundMessage.equalsIgnoreCase("/menu")) {
                    InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(
                            new InlineKeyboardButton("url").url("www.google.com"),
                            new InlineKeyboardButton("callback_data").callbackData("callback_data"),
                            new InlineKeyboardButton("Switch!").switchInlineQuery("switch_inline_query"));
                    SendMessage message = new SendMessage(update.message().chat().id(), "Главное меню");
                    message.replyMarkup(inlineKeyboard);
                    SendResponse response = telegramBot.execute(message);
                    if (!response.isOk()) {
                        logger.error("Response error: {} {}", response.errorCode(), response.message());
                    }
                } else if (inboundMessage.startsWith("/")) { // Analyse the command except '/menu'
                    boolean isFound = false;
                    for (Answer a : answersDb) {
                        if (inboundMessage.equalsIgnoreCase(a.getQuestion())) {
                            isFound = true;
                            SendMessageToTelegram(update, a.getAnswer());
                        }
                    }
                    if (!isFound) {
                        SendMessageToTelegram(update, "Неверная команда\n" + helpMessage);
                    }
                } else { // Parse the notification
                    String dateString, notification;
                    LocalDateTime date;
                    NotificationTask notificationTask = new NotificationTask();
                    Matcher matcher = pattern.matcher(inboundMessage);
                    if (matcher.matches()) {
                        dateString = matcher.group(1);
                        notification = matcher.group(3);
                        try {
                            date = LocalDateTime.parse(dateString, dateTimeFormatterForParsing);
                        } catch (IllegalArgumentException e) {
                            logger.error("Wrong DATE or/and TIME format in the inbound message");
                            SendMessageToTelegram(update, "Неверный формат даты и времени\n" + helpMessage);
                            continue;
                        }
                        if (date.isBefore(LocalDateTime.now())) {  // Checking if the input date is in the past
                            logger.error("Error: DATE and TIME in the past!");
                            SendMessageToTelegram(update, "Дата и время указаны в прошлом\n" + helpMessage);
                            continue;
                        }
                        notificationTask.setChatId(update.message().chat().id());
                        notificationTask.setNotification(notification);
                        notificationTask.setDateTime(date);
                        notificationTask.setSent(false);
                        if (notificationsService.existsByChatIdAndNotificationAndDateTime(update.message().chat().id(),
                                notification, date)) {
                            String errorMessage = "Notification Task with \nchatId: " + update.message().chat().id()
                                    + "\nnotification: " + notification + "\ndate: "
                                    + date.truncatedTo(ChronoUnit.MINUTES).format(dateFormatter) + "\ntime: "
                                    + date.truncatedTo(ChronoUnit.MINUTES).format(timeFormatter)
                                    + "\nwas already saved in the database";
                            logger.error("Notification Task with such parameters was already saved in the database");
                            SendMessageToTelegram(update, errorMessage);
                        } else {
                            notificationsService.save(notificationTask);
                            logger.info("New Notification Task was saved in the database");
                            String str = "Я напомню вам сделать:\n" + notification + "\n"
                                    + date.truncatedTo(ChronoUnit.MINUTES).format(dateFormatter)
                                    + " в "
                                    + date.truncatedTo(ChronoUnit.MINUTES).format(timeFormatter);
                            SendMessageToTelegram(update, str);
                        }
                    } else {
                        SendMessageToTelegram(update, "Неверный формат строки напоминания\n" + helpMessage);
                    }
                }

            } else { // Callback answer processing
                if(update.callbackQuery().data().equals("callback_data")){
                    SendMessageToTelegram(update, "Ай! Ты нажал кнопку 'callback_data'!");
                }
            }

        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public void SendMessageToTelegram(Update update, String textMessage){
        SendMessage message;
        if(update.message() != null) {
            message = new SendMessage(update.message().chat().id(), textMessage);
        } else {
            message = new SendMessage(update.callbackQuery().from().id(), textMessage);
        }
        SendResponse response = telegramBot.execute(message);
        if (!response.isOk()) {
            logger.error("Response error: {} {}", response.errorCode(), response.message());
        }
    }

}
