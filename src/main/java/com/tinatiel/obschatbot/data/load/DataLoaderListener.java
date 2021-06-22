package com.tinatiel.obschatbot.data.load;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Slf4j
public class DataLoaderListener implements ApplicationListener<ContextRefreshedEvent> {

  private final DataLoader dataLoader;
  private boolean completed;

  public DataLoaderListener(DataLoader dataLoader) {
    this.dataLoader = dataLoader;
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
    if(!completed) {
      log.info("Initializing Settings...");
      try{
        dataLoader.loadObsSettings();
      } catch (Exception e) {
        log.error("Unable to initialize settings", e);
      } finally {
        completed = true;
      }
      log.info("...Settings intialized!");
    }
  }
}
