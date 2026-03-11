package demago.khjv2.domain.student.presentation.dto.response.data;

public record StudentDetailResponse(
        Long id,
        String studentNumber,
        String name,
        String bojId,
        int tier,
        int totalSolved,
        double accuracyRate,
        int todaySolved,
        int streak,
        int maxStreak,
        int flame
) {
}
