package com.tinatiel.obschatbot.security;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthorizedClientSchemaLoader implements ApplicationListener<ContextRefreshedEvent> {

  // https://docs.spring.io/spring-security/site/docs/5.4.5/reference/html5/#dbschema-oauth2-client
  private final String ddl = "CREATE TABLE IF NOT EXISTS oauth2_authorized_client (\n"
    + "  client_registration_id varchar(100) NOT NULL,\n"
    + "  principal_name varchar(200) NOT NULL,\n"
    + "  access_token_type varchar(100) NOT NULL,\n"
    + "  access_token_value blob NOT NULL,\n"
    + "  access_token_issued_at timestamp NOT NULL,\n"
    + "  access_token_expires_at timestamp NOT NULL,\n"
    + "  access_token_scopes varchar(1000) DEFAULT NULL,\n"
    + "  refresh_token_value blob DEFAULT NULL,\n"
    + "  refresh_token_issued_at timestamp DEFAULT NULL,\n"
    + "  created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,\n"
    + "  PRIMARY KEY (client_registration_id, principal_name)\n"
    + ");";

  private final DataSource dataSource;
  private boolean completed = false;

  public AuthorizedClientSchemaLoader(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
    if(!completed) {
      log.debug("Initializing OAuth Schema...");
      try{
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.execute(ddl);
        connection.commit();
        connection.close();
      } catch (SQLException sqlException) {
        log.error("Could not fetch connection from datasource: " + dataSource, sqlException);
      }
      completed = true;
      log.debug("...Initialization complete!");
    }
  }

}
