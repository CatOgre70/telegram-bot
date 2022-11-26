package pro.sky.telegrambot.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class AnswerNotFoundException extends RuntimeException{

    public AnswerNotFoundException(String message){
        super(message);
    }

}
