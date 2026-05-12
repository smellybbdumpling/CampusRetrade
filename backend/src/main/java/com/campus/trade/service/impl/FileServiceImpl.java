package com.campus.trade.service.impl;

import com.campus.trade.common.BusinessException;
import com.campus.trade.common.FileStorageProperties;
import com.campus.trade.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private static final Set<String> ALLOWED_SUFFIX = Set.of("jpg", "jpeg", "png", "webp");

    private final FileStorageProperties fileStorageProperties;

    @Override
    public String uploadGoodsImage(MultipartFile file) {
        return uploadImage(file, "图片上传失败");
    }

    @Override
    public String uploadUserAvatar(MultipartFile file) {
        return uploadImage(file, "头像上传失败");
    }

    private String uploadImage(MultipartFile file, String errorMessage) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        String suffix = getFileSuffix(originalFilename);
        if (!ALLOWED_SUFFIX.contains(suffix.toLowerCase())) {
            throw new BusinessException("仅支持 jpg、jpeg、png、webp 格式图片");
        }
        try {
            Path uploadDir = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);
            String fileName = UUID.randomUUID() + "." + suffix;
            Path targetPath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + fileName;
        } catch (IOException exception) {
            throw new BusinessException(errorMessage);
        }
    }

    private String getFileSuffix(String originalFilename) {
        if (!StringUtils.hasText(originalFilename) || !originalFilename.contains(".")) {
            throw new BusinessException("文件名不合法");
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
    }
}