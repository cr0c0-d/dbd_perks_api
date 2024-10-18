package dbd.perks.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${dbd.perk.api.allowed}")
    private String allowed;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://dbd-random-perks-kr.netlify.app", "http://dbd-random-perks-kr.netlify.app", allowed)
                .allowCredentials(true)
                .allowedHeaders("Authorization")
                .allowedMethods("OPTIONS", "GET", "POST", "PUT", "DELETE");

        //		addMapping - CORS를 적용할 url의 패턴을 정의 (/** 로 모든 패턴을 가능하게 함)
        //		allowedOrigins - 허용할 origin을 정의 (* 로 모든 origin을 허용, 여러개도 지정가능)
        //		allowedMethods - HTTP Method를 지정 (* 로 모든 Method를 허용)
        //		maxAge - 원하는 시간만큼 request를 cashing함

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("file:src/main/resources/static/");
    }
}
