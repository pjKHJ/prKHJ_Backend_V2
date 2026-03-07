package demago.khjv2.domain.student.presentation.dto.response;

import java.util.List;
import java.util.Objects;

// 학생 삭제 응답 DTO
public record StudentDeleteResponse(List<Long> deletedIds) {

    public StudentDeleteResponse(List<Long> deletedIds) {
        this.deletedIds = List.copyOf(
                Objects.requireNonNull(deletedIds, "deletedIds는 null일 수 없습니다.")
        );
    }
}