package com.example.pilot_project.config;

import java.io.File;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@EnableIntegration
public class BasicIntegrationConfig {

  @Bean
  public MessageChannel fileChannel() {
    return new DirectChannel();
  }

  @Bean
  @InboundChannelAdapter(value = "fileChannel", poller = @Poller(fixedDelay = "1000"))
  public MessageSource<File> fileReadingMessageSource() {
    FileReadingMessageSource source = new FileReadingMessageSource();
    String inputDirectory = "source_directory";
    source.setDirectory(new File(inputDirectory));
    String filePattern = "*.json";
    source.setFilter(new SimplePatternFileListFilter(filePattern));

    return source;
  }

  @Bean
  @ServiceActivator(inputChannel = "fileChannel")
  public MessageHandler fileWritingMessageHandler() {
    String outputDirectory = "destination_directory";
    FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(outputDirectory));
    handler.setFileExistsMode(FileExistsMode.REPLACE);
    handler.setExpectReply(false);

    return handler;
  }
}
