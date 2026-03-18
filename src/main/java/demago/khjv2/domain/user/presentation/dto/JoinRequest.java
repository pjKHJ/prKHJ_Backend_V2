package demago.khjv2.domain.user.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JoinRequest(
        @NotBlank
        @Size(max = 50)
        String userName,

        @NotBlank
        @Size(min = 4, max = 255)
        String password,

        @NotBlank
        @Size(min = 4, max = 255)
        String signupCode
) {}