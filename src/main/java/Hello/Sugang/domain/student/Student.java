package Hello.Sugang.domain.student;

import Hello.Sugang.domain.student.Difficulty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
@SequenceGenerator(name = "student_seq", sequenceName = "student_seq", initialValue = 1, allocationSize = 1)
public class Student {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_seq")
    @Column(name = "student_id",unique = true)
    private Long id;

    @Column(nullable = false)
    @NotEmpty
    private String email;

    @Column(nullable = false)
    @NotEmpty
    private String password;

    @Column(nullable = false)
    @NotEmpty
    private String name;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    public Student(String name) {
        this.name = name;
        this.email = "dummy@email.com"; // 더미 이메일 기본값
        this.password = "dummy123"; // 더미 비밀번호 기본값
    }


}
