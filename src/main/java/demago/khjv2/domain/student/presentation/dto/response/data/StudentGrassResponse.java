package demago.khjv2.domain.student.presentation.dto.response.data;

import java.util.List;

public record StudentGrassResponse(
        List<StudentGrassItemResponse> grass
) {
    public StudentGrassResponse {
        grass = List.copyOf(grass);
    }
}
