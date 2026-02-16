package demago.khjv2.domain.user.presentation;

import demago.khjv2.domain.user.presentation.dto.JoinRequest;
import demago.khjv2.domain.user.presentation.dto.JoinResponse;
import demago.khjv2.domain.user.presentation.dto.LoginRequest;
import demago.khjv2.domain.user.presentation.dto.LoginResponse;
import demago.khjv2.domain.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/join")
    public ResponseEntity<JoinResponse> join(@Valid @RequestBody JoinRequest request) {
        return ResponseEntity.ok(authService.join(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
