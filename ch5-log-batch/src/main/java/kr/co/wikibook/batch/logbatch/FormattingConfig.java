package kr.co.wikibook.batch.logbatch;

import java.time.format.DateTimeFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;

@Configuration
public class FormattingConfig {

  @Bean
  public FormattingConversionService conversionService() {
    var conversionService = new DefaultFormattingConversionService(true);
    var registrar = new DateTimeFormatterRegistrar();
    registrar.setDateFormatter(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    registrar.registerFormatters(conversionService);
    return conversionService;
  }
}
