package nl.spaan.student_app.repository;

import nl.spaan.student_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    User findByEmail(String email);
    User findUserById(long user);

    List<User> findAllByHouseId(long id);

    void deleteByUsername(String username);

    Boolean existsByHouseIdAndEmail(long id, String email);
}
