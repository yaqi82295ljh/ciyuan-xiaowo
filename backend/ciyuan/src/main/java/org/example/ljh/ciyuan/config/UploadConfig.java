package org.example.ljh.ciyuan.config;

import org.example.ljh.ciyuan.utils.UploadUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class UploadConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String dir = UploadUtils.DIR.getAbsolutePath().replace('\\', '/');

        registry.addResourceHandler("/uploads/**")

                .addResourceLocations("file:" + dir + "/");
    }
}
