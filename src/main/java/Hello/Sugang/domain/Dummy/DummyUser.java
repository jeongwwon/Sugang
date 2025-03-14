package Hello.Sugang.domain.Dummy;

import Hello.Sugang.domain.lecture.Lecture;
import Hello.Sugang.domain.student.Student;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@SequenceGenerator(name = "dummy_user_seq", sequenceName = "dummy_user_seq", initialValue = 100000, allocationSize = 1)
public class DummyUser {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dummy_user_seq")
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
