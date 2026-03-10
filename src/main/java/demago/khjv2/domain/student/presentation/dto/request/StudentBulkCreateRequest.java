package demago.khjv2.domain.student.presentation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record StudentBulkCreateRequest(
        @NotEmpty(message = "students는 1건 이상이어야 합니다.")
        List<@Valid @NotNull StudentCreateRequest> students
) {
    public StudentBulkCreateRequest {
        students = students == null ? null : List.copyOf(students);
    }
}
