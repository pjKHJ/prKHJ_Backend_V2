package demago.khjv2.global.feignclient.boj;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "solvedAcClient",
        url = "${solvedac.base-url}"
)
public interface BojClient {

    @GetMapping("/api/v3/user/grass")
    BojGrassResponse getGrass(
            @RequestParam("handle") String handle,
            @RequestParam(value = "topic", defaultValue = "default") String topic
    );

    @GetMapping("/api/v3/user/show")
    BojDetailsResponse getDetails(
            @RequestParam("handle") String handle
    );
}
