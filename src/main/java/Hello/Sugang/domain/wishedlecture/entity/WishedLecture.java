package Hello.Sugang.domain.wishedlecture.entity;

import Hello.Sugang.domain.lecture.entity.Lecture;
import Hello.Sugang.domain.student.entity.Student;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Data;

@Entity
@Data
public class WishedLecture {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    private double competition;

    private String status;

    @Transactional
    public void setLectureAndStudent(Student student, Lecture lecture,double competition) {
        this.student = student;
        this.lecture = lecture;
        this.competition=competition;
    }

}
