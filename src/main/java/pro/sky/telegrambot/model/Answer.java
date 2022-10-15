package pro.sky.telegrambot.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity(name = "answers")
public class Answer {

    @Id
    private String question;

    private String help;

    private String answer;

    public Answer(String question, String help, String answer){
        this.question = question;
        this.help = help;
        this.answer = answer;
    }

    public Answer() {
        this.question = "";
        this.help = "";
        this.answer = "";
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answer answers = (Answer) o;
        return Objects.equals(question, answers.question) && Objects.equals(help, answers.help) && Objects.equals(answer, answers.answer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question, help, answer);
    }

    @Override
    public String toString() {
        return "Answer{" +
                "question='" + question + '\'' +
                ", help='" + help + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
