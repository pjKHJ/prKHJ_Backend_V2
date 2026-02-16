package demago.khjv2.domain.user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VerificationService {

    private final String expectedCode;

    public VerificationService(@Value("${app.verification.code}") String expectedCode) {
        this.expectedCode = expectedCode;
    }

    public boolean verify(String inputCode) {
        return expectedCode.equals(inputCode);
    }
}
