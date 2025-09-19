package Hello.Sugang.domain.enrollmentLog;

import Hello.Sugang.domain.lecture.Lecture;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class EnrollmentLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @Enumerated(EnumType.STRING)
    private Status status;

    public EnrollmentLog(Long studentId, Lecture lecture) {
        this.studentId = studentId;
        this.lecture = lecture;
    }

}
