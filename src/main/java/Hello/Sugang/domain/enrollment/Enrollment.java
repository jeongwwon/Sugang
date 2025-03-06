package Hello.Sugang.domain.enrollment;

import Hello.Sugang.domain.Dummy.DummyUser;
import Hello.Sugang.domain.lecture.Lecture;
import Hello.Sugang.domain.student.Student;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Enrollment {

    @Id @GeneratedValue
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;


}
