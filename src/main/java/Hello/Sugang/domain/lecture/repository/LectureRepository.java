package Hello.Sugang.domain.lecture.repository;

import Hello.Sugang.domain.lecture.entity.Lecture;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LectureRepository extends JpaRepository<Lecture,Long> {


    //벌크 UPDATE (동시성 문제 방지됨)
    @Modifying
    @Query("UPDATE Lecture l SET l.remainingSeats = l.totalSeats WHERE l.id IN :lectureIds")
    void bulkInitializeLectures(@Param("lectureIds") List<Long> lectureIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM Lecture l WHERE l.id = :lectureId")
    Lecture findByIdForUpdate(@Param("lectureId") Long lectureId);

    @Modifying
    @Query("UPDATE Lecture l SET l.remainingSeats = l.remainingSeats - :count WHERE l.id = :lectureId AND l.remainingSeats >= :count")
    void decreaseSeatsBatch(@Param("lectureId") Long lectureId, @Param("count") int count);

    @Query("SELECT DISTINCT l.school, l.department FROM Lecture l")
    List<Object[]> findDistinctSchoolDepartment();
}
