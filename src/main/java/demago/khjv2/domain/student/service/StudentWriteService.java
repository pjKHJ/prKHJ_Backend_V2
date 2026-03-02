package demago.khjv2.domain.student.service;

import demago.khjv2.domain.student.entity.Student;
import demago.khjv2.domain.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


 //벌크 등록 시 부분 성공을 위한 Write 전용 서비스.
 //각 학생 저장마다 독립 트랜잭션을 사용한다.

@Service
@RequiredArgsConstructor
public class StudentWriteService {

    private final StudentRepository studentRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Student saveAndFlush(final Student student) {
        return studentRepository.saveAndFlush(student);
    }
}
