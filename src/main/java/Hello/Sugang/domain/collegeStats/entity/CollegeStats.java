package Hello.Sugang.domain.collegeStats.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class CollegeStats {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String school;
    private String department;
    private Long studentCount;
    private Long successCount;
    private Long failCount;
}
