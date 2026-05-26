package com.boogieton.nadok.domain.user.controller;

import com.boogieton.nadok.domain.user.dto.UserDto;
import com.boogieton.nadok.domain.user.dto.UserDto.*;
import com.boogieton.nadok.domain.user.entity.User;
import com.boogieton.nadok.domain.user.exception.UserResponseCode;
import com.boogieton.nadok.domain.user.service.UserService;
import com.boogieton.nadok.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public SuccessResponse<ProfileRes> login(@Valid @RequestBody LoginReq loginReq){
        return SuccessResponse.of(userService.login(loginReq), UserResponseCode.LOGIN_SUCCESS);
    }

    @PostMapping("/signup")
    public SuccessResponse<ProfileRes> signup(@Valid @RequestBody SignupReq signupReq){
        return SuccessResponse.of(userService.signup(signupReq), UserResponseCode.SIGNUP_SUCCESS);
    }

    @GetMapping("/check")
    public SuccessResponse<CheckAvailableRes> checkNickname(@RequestParam String nickname){
        return SuccessResponse.of(userService.checkNickname(nickname), UserResponseCode.NICKNAME_SUCCESS);
    }

    @GetMapping("/check/email")
    public SuccessResponse<CheckAvailableRes> checkEmail(@RequestParam String email){
        return SuccessResponse.of(userService.checkEmail(email), UserResponseCode.EMAIL_SUCCESS);
    }

    @DeleteMapping("/{userId}")
    public SuccessResponse<Void> deleteUser(@PathVariable Long userId){
        userService.deleteUser(userId);
        return SuccessResponse.empty(UserResponseCode.DELETE_SUCCESS);
    }

    @PatchMapping("/update/{userId}")
    public SuccessResponse<ProfileRes> updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateReq updateReq) {

        ProfileRes result = userService.updateUser(userId, updateReq);
        return SuccessResponse.of(result, UserResponseCode.UPDATE_SUCCESS);
    }


    @PostMapping("/updateimg/{userId}")
    public SuccessResponse<ProfileRes> updateProfileImg(
            @PathVariable Long userId,
            @RequestParam("profileImg") MultipartFile profileImg) { // String 대신 MultipartFile 사용

        ProfileRes result = userService.updateProfileImg(userId, profileImg);
        return SuccessResponse.of(result, UserResponseCode.UPDATE_SUCCESS);
    }

}
