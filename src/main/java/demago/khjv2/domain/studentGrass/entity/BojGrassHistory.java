package demago.khjv2.domain.studentGrass.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "boj_grass_histories")
public class BojGrassHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 유저의 잔디인지 구분할 값
    @Column(nullable = false, unique = true)
    private String bojId;

    @OneToMany(mappedBy = "bojGrassHistory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BojGrass> grass = new ArrayList<>();

    public BojGrassHistory(String bojId) {
        this.bojId = bojId;
    }

    public void addGrass(BojGrass bojGrass) {
        grass.add(bojGrass);
        bojGrass.setBojGrassHistory(this);
    }

    public void clearGrass() {
        grass.clear();
    }
}