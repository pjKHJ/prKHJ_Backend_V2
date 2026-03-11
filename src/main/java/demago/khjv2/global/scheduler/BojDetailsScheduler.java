package demago.khjv2.global.scheduler;

import demago.khjv2.domain.studentDetail.entity.StudentDetails;
import demago.khjv2.domain.studentDetail.service.StudentDetailsSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class BojDetailsScheduler {

    private final StudentDetailsSyncService studentDetailsSyncService;

    @Scheduled(cron = "0 0 8-22/2 * * *", zone = "Asia/Seoul")
    public void runDailyScrape() {
        studentDetailsSyncService.syncUserDetail();
    }
}