package dev.valente.user_service.userprofile.repository;

import dev.valente.user_service.domain.User;
import dev.valente.user_service.domain.UserProfile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    @Query("SELECT up FROM UserProfile up join fetch up.user u join fetch up.profile p")
    List<UserProfile> retrieveAll();
    //N + 1

//    @EntityGraph(attributePaths = {"user", "profile"})
    @EntityGraph(value = "UserProfile.fullDetails")
    List<UserProfile> findAll();

    @EntityGraph(value = "UserProfile.fullDetails")
    List<UserProfile> findAllByProfile_Id(Long id);

    @Query("SELECT up.user FROM UserProfile up where up.profile.id = ?1")
    List<User> findAllUsersByProfileId(Long id);
}
