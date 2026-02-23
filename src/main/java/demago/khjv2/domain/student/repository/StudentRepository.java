package demago.khjv2.domain.student.repository;

import demago.khjv2.domain.student.entity.Student;
import jakarta.persistence.QueryHint;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;


@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    //학생 중복 체크
    boolean existsByStudentNo(Integer studentNo);

    // 백준 아이디 중복 체크
    boolean existsByBojId(String bojId);


    //학생 삭제 기능은 JpaRepository에 이미 존재하므로 여기에 없어도 됨


    //이 기능들 구현하라고 하진 않았어서 일단 주석 처리 해뒀는데 나중에 추가해도 되냐고 물어봐야지

    //학번으로 학생을 찾아서 돌려주는데, 이 조회는 절대 수정 안 할 거라서
    //‘읽기 전용으로만 처리해줘’라고 미리 알려주는 메서드 임
//    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
//    Optional<Student> findByStudentNo(Integer studentNo);


    // 이거는 위에꺼랑 거의 비슷한데 백준 id로 찾느 메소드이다
//    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
//    Optional<Student> findByBojId(String bojId);
}