package demago.khjv2.domain.student.presentation.dto.response;

import java.util.List;
import java.util.Objects;


//단체 학생 등록 했을 때 클라이언트로 학생들의 정보를
//돌려주는 객체
public record StudentBulkCreateResponse(List<StudentResponse> created) {

    public StudentBulkCreateResponse {
        // null 전달 방지 및 외부 변경으로부터 보호를 위해 불변 리스트로 고정한다.
        Objects.requireNonNull(created, "created must not be null");
        created = List.copyOf(created);
    }
}
