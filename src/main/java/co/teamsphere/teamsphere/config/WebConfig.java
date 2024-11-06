package co.teamsphere.teamsphere.config;

import co.teamsphere.teamsphere.filters.LoggingFilter;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class WebConfig {
    @Bean
    public FilterRegistrationBean<LoggingFilter> loggingFilterRegistration() {
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoggingFilter());
        registrationBean.addUrlPatterns("/*"); // Apply filter to all URLs
        registrationBean.setName("LoggingFilter");
        registrationBean.setOrder(SecurityProperties.DEFAULT_FILTER_ORDER - 1); // Set order if multiple filters
        return registrationBean;
    }
}
