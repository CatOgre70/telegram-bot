package pro.sky.telegrambot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.telegrambot.model.Answer;
import pro.sky.telegrambot.service.AnswersService;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/answers")
public class AnswersController {

    final
    AnswersService answersService;

    public AnswersController(AnswersService answersService) {
        this.answersService = answersService;
    }

    @PostMapping
    public Answer createAnswer(@RequestBody Answer answer){
        return answersService.createAnswer(answer);
    }

    @GetMapping
    public Answer getAnswer(@RequestParam(value = "question") String question) {
        return answersService.getAnswerByQuestion(question);
    }

    @GetMapping("/all")
    public ArrayList<Answer> getAllAnswers() {
        return answersService.getAllAnswers();
    }

    @PutMapping
    public Answer updateAnswer(@RequestBody Answer answer) {
        return answersService.updateAnswer(answer);
    }

    @DeleteMapping
    public Answer deleteAnswer(@RequestBody Answer answer) {
        return answersService.deleteAnswer(answer);
    }

}
