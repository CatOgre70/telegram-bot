package pro.sky.telegrambot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exceptions.AnswerAlreadyExistsException;
import pro.sky.telegrambot.exceptions.AnswerNotFoundException;
import pro.sky.telegrambot.model.Answer;
import pro.sky.telegrambot.repository.AnswersRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnswersService {

    private final AnswersRepository answersRepository;
    private final Logger logger = LoggerFactory.getLogger(AnswersService.class);

    public AnswersService(AnswersRepository answersRepository) {

        this.answersRepository = answersRepository;

    }

    public List<Answer> getAllAnswers(){
        ArrayList<Answer> result = answersRepository.getAllBy();
        if(result.isEmpty()){
            String message = "Answers was not found in the database";
            logger.error(message);
            throw new AnswerNotFoundException(message);
        }
        return result;
    }

    public Answer getAnswerByQuestion(String question){
        Optional<Answer> result = answersRepository.getAnswerByQuestion(question);
        if(result.isEmpty()){
            String message = "Answer on question '" + question + "' was not found in the database";
            logger.error(message);
            throw new AnswerNotFoundException(message);
        }
        return result.get();
    }

    public Answer createAnswer(Answer answer) {
        if(answersRepository.getAnswerByQuestion(answer.getQuestion()).isEmpty()) {
            return answersRepository.save(answer);
        } else {
            String message = "Answer on the question '" + answer.getQuestion() + "' is already in the database";
            logger.error(message);
            throw new AnswerAlreadyExistsException(message);
        }
    }

    public Answer updateAnswer(Answer answer){
        if(answersRepository.getAnswerByQuestion(answer.getQuestion()).isEmpty()) {
            String message = "Answer on the question '" + answer.getQuestion() + "' is not found in the database";
            logger.error(message);
            throw new AnswerNotFoundException(message);
        } else {
            return answersRepository.save(answer);
        }
    }

    public Answer deleteAnswer(Answer answer){
        if(answersRepository.getAnswerByQuestion(answer.getQuestion()).isEmpty()) {
            String message = "Answer on the question '" + answer.getQuestion() + "' is not found in the database";
            logger.error(message);
            throw new AnswerNotFoundException(message);
        } else {
            answersRepository.delete(answer);
            return answer;
        }
    }

}
