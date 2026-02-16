package demago.khjv2.domain.user.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

public record LoginRequest (

    @NotBlank
    @Size(max = 50)
    String username,

    @NotBlank
    @Size(min = 4, max = 255)
    String password
) {}
