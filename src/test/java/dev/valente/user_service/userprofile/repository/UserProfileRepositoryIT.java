package dev.valente.user_service.userprofile.repository;

import dev.valente.user_service.config.IntegrationTestConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserProfileRepositoryIT extends IntegrationTestConfig {

    @Autowired
    private UserProfileRepository userProfileRepository;


    @Test
    @DisplayName("findAll returns a list with all users by profile id")
    @Order(1)
    @Sql("/sql/init_user_profile_2_users_1_profile.sql")
    void findAllUsersByProfileId_ReturnsAllUsersByProfileId_WhenSuccessfull() {
        var profileId = 1L;
        var users = userProfileRepository.findAllUsersByProfileId(profileId);

        Assertions.assertThat(users).isNotEmpty()
                .hasSize(2)
                .doesNotContainNull();

        users.forEach(u -> Assertions.assertThat(u).hasNoNullFieldsOrProperties());
    }

}