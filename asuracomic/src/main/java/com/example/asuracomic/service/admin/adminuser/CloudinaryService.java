package com.example.asuracomic.service.admin.adminuser;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    @Autowired
    private Cloudinary cloudinary;

    public String uploadFile(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return uploadResult.get("url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Tải ảnh thất bại: " + e.getMessage());
        }
    }

    // xo ảnh
    public void deleteFile(String imageUrl) {
        try {
            if (imageUrl == null || imageUrl.isBlank()) return;

            String publicId = extractPublicId(imageUrl);

            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.emptyMap()
            );

        } catch (Exception e) {
            throw new RuntimeException("Xóa ảnh Cloudinary thất bại: " + e.getMessage());
        }
    }

    private String extractPublicId(String imageUrl) {
        // https://res.cloudinary.com/xxx/image/upload/v123456/asura/chapter/page1.jpg
        // => asura/chapter/page1

        String[] parts = imageUrl.split("/upload/");
        if (parts.length < 2) {
            throw new RuntimeException("URL Cloudinary không hợp lệ");
        }

        String path = parts[1];

        // bỏ version v123456
        path = path.replaceFirst("^v\\d+/", "");

        // bỏ đuôi .jpg .png
        return path.substring(0, path.lastIndexOf('.'));
    }


}