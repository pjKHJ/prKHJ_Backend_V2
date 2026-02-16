package demago.khjv2.domain.user.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JoinRequest(
        @Email
        @NotBlank
        @Size(max = 50)
        String username,

        @NotBlank
        @Size(min = 4, max = 255)
        String password,

        @NotBlank
        @Size(min = 4, max = 255)
        String code
) {}