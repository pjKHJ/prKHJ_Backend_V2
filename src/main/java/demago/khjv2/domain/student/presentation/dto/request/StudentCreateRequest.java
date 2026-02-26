package demago.khjv2.domain.student.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCreateRequest {

    //json에 studentNumber 값을 String으로 받아서 저장한다, 만약 값이 없으면 에러가 나도록 한다
    @NotBlank(message = "학번은 필수입니다.")
    @JsonProperty("studentNumber")
    private String studentNumber;

    //json에 name 값을 String으로 받아서 저장한다, 만약 값이 없으면 에러가 나도록 한다
    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 10, message = "이름은 10자 이내여야 합니다.")
    @JsonProperty("name")
    private String name;

    //json에 bojId 값을 String으로 받아서 저장한다, 만약 값이 없으면 에러가 나도록 한다
    @NotBlank(message = "백준 아이디는 필수입니다.")
    @Size(max = 50, message = "백준 아이디는 50자 이내여야 합니다.")
    @JsonProperty("bojId")
    @JsonAlias("bojid")//혹시 몰라서 i가 소문자여도 받을 수 있게 만들어놈
    private String bojId;


    //들어오는 문자열이 null이면 null그대로 반환 해주고 아니면 양쪽 공백을 제거해서 반환해준다
    private static String trim(final String v) {
        return v == null ? null : v.trim();
    }

    //자동으로 실행되는 메소드
    //studentNumber 값에 양쪽에 공백을 제거해준다
    public void setStudentNumber(final String studentNumber) {
        this.studentNumber = trim(studentNumber);
    }

    //자동으로 실행되는 메소드
    //name 값에 양쪽에 공백을 제거해준다
    public void setName(final String name) {
        this.name = trim(name);
    }

    //자동으로 실행되는 메소드
    //bojId 값에 양쪽에 공백을 제거해준다
    public void setBojId(final String bojId) {
        this.bojId = trim(bojId);
    }
}