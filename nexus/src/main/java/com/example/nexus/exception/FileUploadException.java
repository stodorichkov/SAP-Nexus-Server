package com.example.nexus.exception;

import com.example.nexus.constant.MessageConstants;

public class FileUploadException extends RuntimeException {

    public FileUploadException() {
        super(MessageConstants.FILE_UPLOAD_FAILURE);
    }
}
