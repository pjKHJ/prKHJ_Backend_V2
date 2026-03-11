package demago.khjv2.domain.student.presentation.dto.response.data;

import java.time.LocalDate;

public record StudentGrassItemResponse(
        LocalDate date,
        int value
) {
}
