package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.common.UserContext;
import com.campus.trade.dto.UserAvatarVO;
import com.campus.trade.service.FileService;
import com.campus.trade.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final UserService userService;

    @PostMapping("/upload")
    public Result<String> uploadGoodsImage(@RequestParam("file") MultipartFile file) {
        return Result.success("上传成功", fileService.uploadGoodsImage(file));
    }

    @PostMapping("/avatar")
    public Result<UserAvatarVO> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String avatar = fileService.uploadUserAvatar(file);
        userService.updateAvatar(UserContext.getRequiredUserId(), avatar);
        return Result.success("头像上传成功", new UserAvatarVO(avatar));
    }
}