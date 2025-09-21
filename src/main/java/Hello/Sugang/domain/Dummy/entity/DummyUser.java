package Hello.Sugang.domain.Dummy.entity;

import Hello.Sugang.domain.lecture.entity.Lecture;
import Hello.Sugang.domain.student.entity.Student;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class DummyUser {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="lecture_id")
    private Lecture lecture;

    public DummyUser(Student student, Lecture lecture) {
        this.student = student;
        this.lecture = lecture;
    }
}
