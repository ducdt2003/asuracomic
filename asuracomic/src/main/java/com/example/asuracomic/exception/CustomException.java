package com.example.asuracomic.exception;


/**
 * Lớp xử lý lỗi tùy chỉnh cho ứng dụng.
 */
public class CustomException extends RuntimeException {
    /**
     * Tạo CustomException với thông báo lỗi.
     *
     * @param message Thông báo lỗi
     */
    public CustomException(String message) {
        super(message);
    }
}