package unit_test.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unit_test.model.GymMember;

@Repository
public interface GymMemberRepository extends JpaRepository<GymMember, Long> {
    Optional<GymMember> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
}
