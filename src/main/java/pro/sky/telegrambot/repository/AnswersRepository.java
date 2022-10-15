package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.Answer;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface AnswersRepository extends JpaRepository<Answer, String> {

    Optional<Answer> getAnswerByQuestion(String question);
    Optional<ArrayList<Answer>> getAllBy();

}
