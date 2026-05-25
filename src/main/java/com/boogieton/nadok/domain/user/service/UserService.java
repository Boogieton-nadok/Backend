package com.boogieton.nadok.domain.user.service;

import com.boogieton.nadok.domain.user.dto.UserDto.*;
import com.boogieton.nadok.domain.user.entity.User;
import com.boogieton.nadok.domain.user.exception.UserResponseCode;
import com.boogieton.nadok.domain.user.repository.UserRepository;
import com.boogieton.nadok.global.exception.BaseException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    @Value("${file.upload-dir}")
    private String uploadDir;

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
        if(signupReq.getPassword().length() < 6 || signupReq.getPassword().length() > 8) {
            throw new BaseException(UserResponseCode.PASSWORD_TYPE_ERROR);
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
    public ProfileRes updateProfileImg(Long userId, MultipartFile profileImg) {
        // 1. 유저 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(UserResponseCode.USER_NOT_FOUND));

        // 2. 파일이 비어있는지 확인
        if (profileImg == null || profileImg.isEmpty()) {
            throw new BaseException(UserResponseCode.INVALID_FILE); // 적절한 예외 코드를 사용하세요
        }

        // 3. 로컬에 저장할 경로 설정 (예: 프로젝트 루트 폴더 안의 uploads 폴더)

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs(); // 해당 디렉토리가 없으면 생성
        }

        // 4. 파일명 중복 방지를 위해 UUID 생성 및 확장자 추출
        String originalFilename = profileImg.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedFilename = UUID.randomUUID().toString() + extension;

        // 5. 로컬 파일 시스템에 파일 저장 (uploadDir 사용)
        File destinationFile = new File(uploadDir + savedFilename);
        try {
            profileImg.transferTo(destinationFile);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }

        // 💡 6. DB에 저장할 접근 경로(URL) 정의 수정
        // WebConfig의 addResourceHandler("/uploads/profiles/**")와 매칭되는 웹 경로
        String profileImgUrl = "/uploads/profiles/" + savedFilename;

        // 7. 엔티티 상태 변경 (Dirty Checking으로 인해 자동 업데이트)
        user.updateProfileImg(profileImgUrl);

        // 8. DTO 반환
        return ProfileRes.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImgUrl(user.getProfileImgUrl())
                .build();
    }
}
