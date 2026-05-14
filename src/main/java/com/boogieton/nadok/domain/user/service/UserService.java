package com.boogieton.nadok.domain.user.service;

import com.boogieton.nadok.domain.user.dto.UserDto.*;
import com.boogieton.nadok.domain.user.entity.User;
import com.boogieton.nadok.domain.user.exception.UserResponseCode;
import com.boogieton.nadok.domain.user.repository.UserRepository;
import com.boogieton.nadok.global.exception.BaseException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ProfileRes login(@Valid LoginReq loginReq) {
        User user = userRepository.findByEmail(loginReq.getEmail());
        if (user == null) {
            throw new BaseException(UserResponseCode.USER_NOT_FOUND);
        }
        if (!user.getPassword().equals(loginReq.getPassword())) {
            throw new BaseException(UserResponseCode.LOGIN_FALSE);
        }
        ProfileRes profileRes = ProfileRes.fromEntity(user);

        return profileRes;
    }

    @Transactional
    public ProfileRes signup(@Valid SignupReq signupReq) {
        if (userRepository.existsByEmail(signupReq.getEmail())) {
            throw new BaseException(UserResponseCode.EMAIL_DUPLICATION);
        }
        if (userRepository.existsByNickname(signupReq.getNickname())) {
            throw new BaseException(UserResponseCode.NICKNAME_DUPLICATION);
        }
        User user = User.builder()
                .email(signupReq.getEmail())
                .password(signupReq.getPassword())
                .nickname(signupReq.getNickname())
                .build();

        try {
            User saveUser = userRepository.save(user);
            ProfileRes profileRes = ProfileRes.fromEntity(saveUser);
            if (profileRes == null) {
                throw new BaseException(UserResponseCode.SIGNUP_FALSE);
            }
            return profileRes;
        } catch (DataIntegrityViolationException e) {
            throw new BaseException(UserResponseCode.EMAIL_DUPLICATION);
        }
    }

    @Transactional(readOnly = true)
    public CheckAvailableRes checkNickname(String nickname) {
        User user = userRepository.findByNickname(nickname);
        if (user != null) {
            throw new BaseException(UserResponseCode.NICKNAME_DUPLICATION);
        }
        return new CheckAvailableRes(true);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(UserResponseCode.USER_NOT_FOUND));
        userRepository.delete(user);
    }

    @Transactional
    public ProfileRes updateUser(Long userId, UpdateReq updateReq) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(UserResponseCode.USER_NOT_FOUND));

        if (updateReq.getNickname() != null) {
            if (userRepository.existsByNicknameAndUserIdNot(updateReq.getNickname(), userId)) {
                throw new BaseException(UserResponseCode.NICKNAME_DUPLICATION);
            }
        }
        user.updateProfile(
                updateReq.getNickname(),
                updateReq.getPassword(),
                updateReq.getGender(),
                updateReq.getBirthday()
        );

        return ProfileRes.fromEntity(user);
    }
}
