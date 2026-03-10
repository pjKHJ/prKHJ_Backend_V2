package demago.khjv2.domain.student.presentation.dto.response.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ClassCompetitionItemResponse(
        @JsonProperty("class")
        int classNumber,
        int tier,
        int totalSolved,
        double accuracyRate,
        int todaySolved,
        int streak,
        int maxStreak
) {
}
