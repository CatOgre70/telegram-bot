package pro.sky.telegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import pro.sky.telegrambot.sender.TelegramBotNotificationsSender;

@SpringBootApplication
@EnableScheduling
public class TelegramBotApplication {

	public static void main(String[] args) {

		SpringApplication.run(TelegramBotApplication.class, args);

	}

}
