package demago.khjv2.domain.studentDetail.repository;

import demago.khjv2.domain.studentDetail.entity.StudentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentDetailsRepository extends JpaRepository<StudentDetails,Integer> {
    Optional<String> findByUsername(String username);
}
