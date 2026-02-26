package demago.khjv2.domain.student.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public record StudentCreateRequest(
        @JsonProperty("studentNumber")
        String studentNumber,

        @JsonProperty("name")
        String name,

        @JsonProperty("bojId")
        @JsonAlias("bojid")   // bojId를 소문자로도 받을 수 있게 추가
        String bojId
) {


    public StudentCreateRequest {
        studentNumber = trim(studentNumber);
        name = trim(name);
        bojId = trim(bojId);
    }

    private static String trim(String value) {
        if (value == null) {
            return null;
        }
        return value.trim();
    }
}