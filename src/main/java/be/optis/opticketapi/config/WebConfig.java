package be.optis.opticketapi.config;

import be.optis.opticketapi.util.BarSeparatedStringToListConverter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private BarSeparatedStringToListConverter converter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(converter);
    }
}
