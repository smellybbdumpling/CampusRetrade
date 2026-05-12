package com.campus.trade.service;

public interface FileService {

    String uploadGoodsImage(org.springframework.web.multipart.MultipartFile file);

    String uploadUserAvatar(org.springframework.web.multipart.MultipartFile file);
}