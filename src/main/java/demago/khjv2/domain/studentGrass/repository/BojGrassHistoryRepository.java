package demago.khjv2.domain.studentGrass.repository;

import demago.khjv2.domain.studentGrass.entity.BojGrassHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BojGrassHistoryRepository extends JpaRepository<BojGrassHistory, Long> {
    Optional<BojGrassHistory> findByBojId(String bojId);
}