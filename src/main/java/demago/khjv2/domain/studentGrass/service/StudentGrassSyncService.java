package demago.khjv2.domain.studentGrass.service;

import demago.khjv2.domain.student.entity.Student;
import demago.khjv2.domain.student.repository.StudentRepository;
import demago.khjv2.domain.studentGrass.entity.BojGrass;
import demago.khjv2.domain.studentGrass.entity.BojGrassHistory;
import demago.khjv2.domain.studentGrass.repository.BojGrassHistoryRepository;
import demago.khjv2.global.feignclient.boj.BojClient;
import demago.khjv2.global.feignclient.boj.BojGrassResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentGrassSyncService {

    private final StudentRepository studentRepository;
    private final BojGrassHistoryRepository bojGrassHistoryRepository;
    private final BojClient bojClient;
    private final TransactionTemplate transactionTemplate;

    public void syncGrass() {
        List<Student> studentList = studentRepository.findAll();

        for (Student student : studentList) {
            String bojId = student.getBojId();

            if (bojId == null || bojId.isBlank()) {
                continue;
            }

            try {
                BojGrassResponse response = bojClient.getGrass(bojId, "default");

                if (response == null || response.grass() == null) {
                    continue;
                }

                transactionTemplate.executeWithoutResult(status -> {
                    BojGrassHistory history = bojGrassHistoryRepository.findByBojId(bojId)
                            .orElseGet(() -> new BojGrassHistory(bojId));

                    history.clearGrass();

                    for (BojGrassResponse.GrassDto dto : response.grass()) {
                        history.addGrass(new BojGrass(dto.date(), dto.value()));
                        }

                    bojGrassHistoryRepository.save(history);
                });

            } catch (Exception e) {
                log.warn("잔디 동기화 실패 bojId = {}", bojId, e);
            }
        }
    }
}