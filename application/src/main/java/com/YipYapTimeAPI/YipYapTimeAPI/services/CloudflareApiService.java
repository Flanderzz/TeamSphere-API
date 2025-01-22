package com.YipYapTimeAPI.YipYapTimeAPI.services;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.YipYapTimeAPI.YipYapTimeAPI.response.CloudflareApiResponse;

@Service
public interface CloudflareApiService {
    CloudflareApiResponse uploadImage(MultipartFile imageFile) throws IOException;
    CloudflareApiResponse deleteImage(String imageId);
}

