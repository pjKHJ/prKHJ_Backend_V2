package demago.khjv2.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false, unique = true)
    private String name;

    @Column(length = 255, nullable = false)
    private String password;

    public static User of(String name, String encodedPassword) {
        return User.builder()
                .name(name)
                .password(encodedPassword)
                .build();
    }
}
