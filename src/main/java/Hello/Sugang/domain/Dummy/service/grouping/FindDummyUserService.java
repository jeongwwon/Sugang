package Hello.Sugang.domain.Dummy.service.grouping;

import Hello.Sugang.domain.Dummy.entity.DummyUser;
import Hello.Sugang.domain.Dummy.repository.DummyUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * studentId로 DummyUser 조회 후 Lecture별 그룹화
 */
@Service
@AllArgsConstructor
public class FindDummyUserService {

    private final DummyUserRepository dummyUserRepository;

    public Map<Long, List<Long>> groupDummyUsersByLecture(Long studentId) {
        return dummyUserRepository.findByStudentId(studentId)
                .stream()
                .collect(Collectors.groupingBy(
                        dummyUser -> dummyUser.getLecture().getId(),
                        Collectors.mapping(DummyUser::getId, Collectors.toList())
                ));
        // {Key엔 lectureId,Value엔 더미유저 pk 리스트}
    }

}
