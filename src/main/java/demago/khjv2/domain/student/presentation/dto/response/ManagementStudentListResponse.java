package demago.khjv2.domain.student.presentation.dto.response;

import java.util.List;

public record ManagementStudentListResponse(
        List<StudentResponse> students
) {
    public ManagementStudentListResponse {
        students = List.copyOf(students);
    }
}
