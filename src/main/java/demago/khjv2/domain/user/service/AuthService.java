package demago.khjv2.domain.user.service;

import demago.khjv2.domain.user.entity.User;
import demago.khjv2.domain.user.exception.UserErrorCode;
import demago.khjv2.domain.user.presentation.dto.JoinRequest;
import demago.khjv2.domain.user.presentation.dto.JoinResponse;
import demago.khjv2.domain.user.presentation.dto.LoginRequest;
import demago.khjv2.domain.user.presentation.dto.LoginResponse;
import demago.khjv2.domain.user.repository.UserRepository;
import demago.khjv2.global.error.exception.KHJException;
import demago.khjv2.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final VerificationService verificationService;

    @Transactional
    public JoinResponse join(JoinRequest request) {

//        if (memberRepository.existsByEmail(request.email())) {
//            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
//        }

        if (verificationService.verify(request.code())) {
            throw new KHJException(UserErrorCode.INVALID_VERIFICATION_CODE);
        }

        User user = User.of(
                request.username(),
                passwordEncoder.encode(request.password())
        );

        try {
            User saved = userRepository.save(user);

            return new JoinResponse(saved.getUsername());
        } catch (DataIntegrityViolationException e) {
            throw new KHJException(UserErrorCode.USERNAME_ERROR_CODE);

        }
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new KHJException(UserErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new KHJException(UserErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername());
        return new LoginResponse(accessToken, "");
    }
}