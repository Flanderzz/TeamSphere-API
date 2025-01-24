package co.teamsphere.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app.environment")
@Data
public class AppProperties {

    private List<String> allowedOrigins;
}
