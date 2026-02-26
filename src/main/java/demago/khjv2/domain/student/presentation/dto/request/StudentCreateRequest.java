package demago.khjv2.domain.student.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public record StudentCreateRequest(

        //json의 stduentNumber이라는 키 값에 value를 studentNumber이라는 변수에 저장함
        @JsonProperty("studentNumber")
        String studentNumber,

        //json의 name 키 값에 value를 name 변수에 저장함
        @JsonProperty("name")
        String name,

        //json의 bojId 키 값에 value를 bojId 변수에 저장함
        @JsonProperty("bojId")
        @JsonAlias("bojid")   // bojId를 소문자로도 받을 수 있게 추가
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