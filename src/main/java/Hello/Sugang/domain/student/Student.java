package Hello.Sugang.domain.student;

import Hello.Sugang.domain.student.Difficulty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Data
@Getter
@NoArgsConstructor
public class Student {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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
