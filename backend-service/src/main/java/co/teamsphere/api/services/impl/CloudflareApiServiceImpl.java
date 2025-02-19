package co.teamsphere.api.services.impl;

import co.teamsphere.api.exception.CloudflareException;
import co.teamsphere.api.response.CloudflareApiResponse;
import co.teamsphere.api.services.CloudflareApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Service
@Slf4j
public class CloudflareApiServiceImpl implements CloudflareApiService {
    @Value("${cloudflare.api.key}")
    private String cloudflareApiKey;
    @Value("${cloudflare.api.accountID}")
    private String cloudflareAccountID;
    private final String apiUrl = "https://api.cloudflare.com/client/v4/accounts/{account_id}/images/v1";
    private final RestTemplate restTemplate;

    public CloudflareApiServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public CloudflareApiResponse uploadImage(MultipartFile imageFile) throws IOException  {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", String.format("Bearer %s", cloudflareApiKey));

        var body = new LinkedMultiValueMap<String, Object>();
        final String filename = UUID.randomUUID().toString();

        body.add("file", new ByteArrayResource(imageFile.getBytes()) {
            @Override
            public String getFilename() {
                return filename;
            }
        });

        body.add("requireSignedURLs", false);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        String url = apiUrl.replace("{account_id}", cloudflareAccountID);
        ResponseEntity<CloudflareApiResponse> response;

        try {
            log.info("Sending ImageID: {} request to URL: {}",filename , url);
            response = restTemplate.postForEntity(url, requestEntity, CloudflareApiResponse.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("{} uploaded successfully to Cloudflare.", filename);
                return response.getBody();
            }

            log.error("Failed to upload image to Cloudflare. Status code: {}", response.getStatusCode());
            return createErrorResponse("Upload failed with status: " + response.getStatusCode());

        } catch (HttpClientErrorException e) {
            log.error("Cloudflare API client error: {}", e.getResponseBodyAsString());

            try {
                // Parse the error response into our API response object
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(e.getResponseBodyAsString(), CloudflareApiResponse.class);
            } catch (JsonProcessingException jsonException) {
                log.error("Failed to parse Cloudflare error response", jsonException);
                return createErrorResponse("Authentication failed: " + e.getStatusCode());
            }

        } catch (RestClientException e) {
            // Handle other REST client exceptions (network issues, etc.)
            log.error("REST client error while uploading to Cloudflare", e);
            return createErrorResponse("Failed to communicate with Cloudflare: " + e.getMessage());

        } catch (Exception e) {
            // Catch any other unexpected exceptions
            log.error("Unexpected error while uploading to Cloudflare", e);
            return createErrorResponse("Internal server error");
        }
    }

    @Override
    public CloudflareApiResponse deleteImage(String imageID) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", String.format("Bearer %s", cloudflareApiKey));

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
    
        String url = apiUrl.replace("{account_id}", cloudflareAccountID) + "/" + imageID;
    
        CloudflareApiResponse cloudflareApiResponse = new CloudflareApiResponse();
    
        try {
            log.info("Sending DELETE request to URL: {}", url);
            ResponseEntity<CloudflareApiResponse> response = restTemplate.exchange(
                url, 
                org.springframework.http.HttpMethod.DELETE, 
                requestEntity, 
                CloudflareApiResponse.class
            );
    
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("ImageID: {} deleted successfully from Cloudflare.", imageID);
                return response.getBody();
            }
            log.error("Failed to delete image from Cloudflare. Status code: {}", response.getStatusCode());
        } catch (HttpClientErrorException e) {
            log.error("Cloudflare API error: {}", e.getResponseBodyAsString());
            createErrorResponse(e.getMessage());
        }
        return cloudflareApiResponse;
    }

    private CloudflareApiResponse createErrorResponse(String message) {
        CloudflareApiResponse errorResponse = new CloudflareApiResponse();
        errorResponse.setSuccess(false);
        CloudflareException error = new CloudflareException();
        error.setMessage(message);
        errorResponse.setErrors(Collections.singletonList(error));
        return errorResponse;
    }
}
    