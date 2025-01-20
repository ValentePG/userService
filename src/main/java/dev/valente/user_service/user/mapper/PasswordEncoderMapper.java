package dev.valente.user_service.user.mapper;

import dev.valente.user_service.annotation.EncoderMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PasswordEncoderMapper {
    private final PasswordEncoder passwordEncoder;

    @EncoderMapping
    public String encode(String rawPassword) {
        return rawPassword == null ? null : passwordEncoder.encode(rawPassword);
    }
}
