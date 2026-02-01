package com.extension.AITranslatorExtension.config;

import com.extension.AITranslatorExtension.filter.RateLimitingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitFilter(RateLimitingFilter rateLimitingFilter) {
        FilterRegistrationBean<RateLimitingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(rateLimitingFilter);
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(1);
        registrationBean.setName("rateLimitingFilter");
        return registrationBean;
    }
}
