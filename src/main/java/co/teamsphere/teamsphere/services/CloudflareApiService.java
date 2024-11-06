package co.teamsphere.teamsphere.services;

import co.teamsphere.teamsphere.models.User;
import co.teamsphere.teamsphere.response.CloudflareApiResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface CloudflareApiService {
    CloudflareApiResponse uploadImage(MultipartFile imageFile, User user) throws IOException;
}

