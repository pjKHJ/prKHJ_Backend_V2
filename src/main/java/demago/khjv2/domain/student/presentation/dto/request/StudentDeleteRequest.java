package demago.khjv2.domain.student.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public record StudentDeleteRequest(
        //json으로 온 데이터중에 ids로 온것을 여기에 넣어라
        @JsonProperty("ids")
        List<Long> ids
) {

    //데이터가 null인지 확인한 후 아니면 데이터 오염 방지를 위해 얕은 복사를 한다
    public StudentDeleteRequest {
        if (ids != null) {
            ids = Collections.unmodifiableList(new ArrayList<>(ids));
        }
    }
}