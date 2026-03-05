package demago.khjv2.domain.student.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StudentCreateRequest(

        //json의 stduentNumber이라는 키 값에 value를 studentNumber이라는 변수에 저장함
        @JsonProperty("studentNumber")
        @NotBlank(message = "studentNumber는 필수입니다.")
        @Pattern(regexp = "[1-3]\\d{3}", message = "studentNumber는 1000~3999 범위의 4자리 숫자여야 합니다.")
        String studentNumber,

        //json의 name 키 값에 value를 name 변수에 저장함
        @JsonProperty("name")
        @NotBlank(message = "name은 필수입니다.")
        @Size(max = 10, message = "name은 10자 이하여야 합니다.")
        String name,

        //json의 bojId 키 값에 value를 bojId 변수에 저장함
        @JsonProperty("bojId")
        @JsonAlias("bojid")   // bojId를 소문자로도 받을 수 있게 추가
        @NotBlank(message = "bojId는 필수입니다.")
        @Size(max = 50, message = "bojId는 50자 이하여야 합니다.")
        String bojId
) {

    //사용자가 입력한 값에 공백이 있으면 trim으로 제거해주는 곳
    public StudentCreateRequest {
        studentNumber = trim(studentNumber);
        name = trim(name);
        bojId = trim(bojId);
    }

    //만약에 사용자로 부터 받은 값에 null이 있으면 그냥 null을 반환 해준다
    private static String trim(String value) {
        if (value == null) {
            return null;
        }
        return value.trim();
    }
}