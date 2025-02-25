package co.teamsphere.api.config;

import org.springframework.beans.factory.annotation.Value;

public class JWTTokenConst {
    @Value("${env.JWT_AUTH}")
    public static final String HEADER = "Authorization";
}
