package demago.khjv2.domain.student.presentation.dto.response.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ClassCompetitionResponse(
        @JsonProperty("class")
        List<ClassCompetitionItemResponse> classList
) {
    public ClassCompetitionResponse {
        classList = List.copyOf(classList);
    }
}
