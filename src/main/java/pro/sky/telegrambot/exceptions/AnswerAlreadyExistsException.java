package pro.sky.telegrambot.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class AnswerAlreadyExistsException extends RuntimeException{

    public AnswerAlreadyExistsException(String message){
        super(message);
    }

}
