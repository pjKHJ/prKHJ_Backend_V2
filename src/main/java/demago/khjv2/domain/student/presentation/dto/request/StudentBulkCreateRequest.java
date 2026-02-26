package demago.khjv2.domain.student.presentation.dto.request;

import java.util.List;

public record StudentBulkCreateRequest(
        List<StudentCreateRequest> students
) {
}