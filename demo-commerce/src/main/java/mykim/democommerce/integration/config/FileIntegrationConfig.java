package mykim.democommerce.integration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;

import java.io.File;

@Configuration
public class FileIntegrationConfig {

  @Value("${app.product.file.input.directory:${user.home}/demo-commerce/input}")
  private String inputDirectory;

  @Value("${app.product.file.processed.directory:${user.home}/demo-commerce/processed}")
  private String processedDirectory;

  @Bean
  @InboundChannelAdapter(value = "productFileInputChannel", poller = @Poller(fixedDelay = "5000"))
  public MessageSource<File> fileReadingMessageSource() {
    FileReadingMessageSource source = new FileReadingMessageSource();
    source.setDirectory(new File(inputDirectory));
    source.setFilter(new SimplePatternFileListFilter("*.csv"));
    source.setAutoCreateDirectory(true);

    return source;
  }

  @Bean
  public File inputDirectory() {
    File dir = new File(inputDirectory);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    return dir;
  }

  @Bean
  public File processedDirectory() {
    File dir = new File(processedDirectory);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    return dir;
  }
}
