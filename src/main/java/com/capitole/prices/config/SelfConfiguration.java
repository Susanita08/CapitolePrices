package com.capitole.prices.config;

import io.netty.channel.ChannelOption;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import reactor.netty.http.client.HttpClient;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Pattern;
import java.time.format.DateTimeFormatter;

@Configuration
@Data
public class SelfConfiguration implements WebMvcConfigurer {

    @Value("${capitole.tax}")
    @Pattern(regexp ="[0,9]+")
    @Digits(integer = 0, fraction =2)
    private double tax;

    @Value("${capitole.internal.timeout}")
    private Integer internalTimeout;

    @Bean
    @Primary
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, getInternalTimeout());
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setDateFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss"));
        registrar.registerFormatters(registry);
    }

}
