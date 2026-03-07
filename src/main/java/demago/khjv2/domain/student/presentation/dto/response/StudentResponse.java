package demago.khjv2.domain.student.presentation.dto.response;

//학생 한 명 정보를 줄때 사용하는 데이터 틀
public record StudentResponse(
        Long id,
        String studentNumber,
        String name,
        String bojId
) {
}