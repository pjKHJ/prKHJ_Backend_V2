package demago.khjv2.global.scheduler;

import demago.khjv2.domain.studentGrass.service.StudentGrassSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BojGrassScheduler {

    private final StudentGrassSyncService studentGrassSyncService;

    @Scheduled(cron = "0 0 8-22/2 * * *", zone = "Asia/Seoul")
    public void syncGrass() {
        studentGrassSyncService.syncGrass();
    }
}
