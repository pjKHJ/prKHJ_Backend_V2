package demago.khjv2.global.security.userdetails;

import demago.khjv2.domain.user.entity.User;
import demago.khjv2.domain.user.exception.UserErrorCode;
import demago.khjv2.domain.user.repository.UserRepository;
import demago.khjv2.global.error.exception.KHJException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new KHJException(UserErrorCode.USER_ERROR_CODE));
        return CustomUserDetails.from(user);
    }

    // JWT subject(memberId) 기반 인증을 위해 추가한 메서드
    public CustomUserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new KHJException(UserErrorCode.USER_ERROR_CODE));
        return CustomUserDetails.from(user);
    }
}
