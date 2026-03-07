package demago.khjv2.domain.student.presentation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

public record StudentBulkCreateRequest(
        @NotEmpty(message = "students는 1건 이상이어야 합니다.")
        List<@Valid @NotNull StudentCreateRequest> students
) {
    public StudentBulkCreateRequest {
        students = List.copyOf(
                Objects.requireNonNull(students, "students는 null일 수 없습니다.")
        );
    }
}