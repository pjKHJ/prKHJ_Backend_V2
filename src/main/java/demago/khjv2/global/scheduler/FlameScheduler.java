package demago.khjv2.global.scheduler;

import demago.khjv2.domain.studentDetail.service.StudentDetailsSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FlameScheduler {

    private final StudentDetailsSyncService studentDetailsSyncService;

    @Scheduled(cron = "0 55 23 * * *", zone = "Asia/Seoul")
    public void syncFlame() {
        studentDetailsSyncService.flame();
    }
}
