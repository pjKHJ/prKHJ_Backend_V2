package demago.khjv2.domain.student.service;

import demago.khjv2.domain.student.entity.Student;
import demago.khjv2.domain.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


 //벌크 등록할 때 중복 학생은 스킵하고 성공한 학생들만 저장하려고 만든 서비스

 //한 명 실패해도 전체가 망가지지 않게 각 저장마다 새로운 트랜잭션을 함

@Service
@RequiredArgsConstructor
public class StudentWriteService {

    private final StudentRepository studentRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Student saveAndFlush(final Student student) {
        return studentRepository.saveAndFlush(student);
    }
}
