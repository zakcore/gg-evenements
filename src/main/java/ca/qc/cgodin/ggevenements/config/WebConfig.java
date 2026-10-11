package ca.qc.cgodin.ggevenements.config;

import ca.qc.cgodin.ggevenements.service.FichierService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final FichierService fichierService;

    public WebConfig(FichierService fichierService) {
        this.fichierService = fichierService;
    }

    /** Les affiches téléversées sont servies depuis le dossier configuré (app.uploads.dir). */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(fichierService.getDossier().toUri().toString());
    }

    /** Dates et nombres toujours formatés en français canadien. */
    @Bean
    public LocaleResolver localeResolver() {
        return new FixedLocaleResolver(Locale.CANADA_FRENCH);
    }
}
