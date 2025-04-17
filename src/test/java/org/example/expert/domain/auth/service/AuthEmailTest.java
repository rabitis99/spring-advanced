package org.example.expert.domain.auth.service;

import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AuthEmailTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    public void email이_중복일때_에러가_발생한다() {
        // given
        String email = "email@example.com";
        SignupRequest signupRequest = new SignupRequest(email, "password", "user");
        given(userRepository.existsByEmail(any())).willReturn(true);

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            authService.signup(signupRequest);
        });

        // then
        assertEquals("이미 존재하는 이메일입니다.", exception.getMessage(), "예상하지 못한 값인데요");
    }

    @Test
    public void email_정상() {
        // given
        String email = "email@example.com";
        String rawPassword = "passWord";
        String encodePassword = "encode-password";
        String bearerToken = "token";

        SignupRequest signupRequest = new SignupRequest(email, rawPassword, "user");

        User savedUser = new User(email, encodePassword, UserRole.USER);
        //gpt 선생 -> null 방지
        ReflectionTestUtils.setField(savedUser, "id", 1L); // ID 설정

        given(userRepository.existsByEmail(any())).willReturn(false);
        given(passwordEncoder.encode(any())).willReturn(encodePassword);
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        given(jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), savedUser.getUserRole()))
                .willReturn(bearerToken);

        // when
        SignupResponse signupResponse = authService.signup(signupRequest);

        // then

        assertEquals(bearerToken, signupResponse.getBearerToken(), "토큰이 예상과 다릅니다");
    }
}


