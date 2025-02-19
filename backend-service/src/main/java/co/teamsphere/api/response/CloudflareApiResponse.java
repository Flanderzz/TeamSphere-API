package co.teamsphere.api.response;

import co.teamsphere.api.exception.CloudflareException;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CloudflareApiResponse {

    private Result result;
    private boolean success;
    private List<CloudflareException> errors;
    private List<String> messages;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result {

        private String id;
        private String filename;
        private Map<String, String> metadata;
        private LocalDateTime uploaded;
        private boolean requireSignedURLs;
        private List<String> variants;

        @JsonProperty("id")
        public String getId() {
            return id;
        }

        @JsonProperty("filename")
        public String getFilename() {
            return filename;
        }

        @JsonProperty("metadata")
        public Map<String, String> getMetadata() {
            return metadata;
        }

        @JsonProperty("uploaded")
        public LocalDateTime getUploaded() {
            return uploaded;
        }

        @JsonProperty("requireSignedURLs")
        public boolean isRequireSignedURLs() {
            return requireSignedURLs;
        }

        @JsonProperty("variants")
        public List<String> getVariants() {
            return variants;
        }
    }
}

