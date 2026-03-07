package demago.khjv2.global.feignclient.boj;

import java.time.LocalDate;
import java.util.List;

public record BojGrassResponse(
        List<GrassDto> grass,
        int currentStreak,
        int longestStreak,
        String topic
) {
    public record GrassDto(
            LocalDate date,
            int value
    ) {}
}
