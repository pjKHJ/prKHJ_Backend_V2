package demago.khjv2.domain.student.presentation.dto.response.data;

import java.util.List;

public record StudentListResponse(
        List<StudentListItemResponse> students
) {
    public StudentListResponse {
        students = List.copyOf(students);
    }
}
