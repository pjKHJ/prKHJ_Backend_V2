package demago.khjv2.domain.studentGrass.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "boj_grass")
public class BojGrass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Integer value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boj_grass_history_id", nullable = false)
    private BojGrassHistory bojGrassHistory;

    public BojGrass(LocalDate date, Integer value) {
        this.date = date;
        this.value = value;
    }

    public void setBojGrassHistory(BojGrassHistory bojGrassHistory) {
        this.bojGrassHistory = bojGrassHistory;
    }
}