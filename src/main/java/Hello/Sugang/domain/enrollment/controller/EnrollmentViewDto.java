package Hello.Sugang.domain.enrollment.controller;

import Hello.Sugang.domain.lecture.entity.Lecture;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
public class EnrollmentViewDto {
    private final Long studentId;
    private final List<Lecture> lectures;
    private final List<Integer> competitions;
    private final Set<Long> enrollmentSet;
}
