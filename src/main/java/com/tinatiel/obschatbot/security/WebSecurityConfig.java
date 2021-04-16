package com.tinatiel.obschatbot.security;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Configure application-wide HTTP security, including enabling OAuth2 Client features.
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        // All web requests are served and consumed on localhost
        .authorizeRequests().anyRequest().permitAll()

        // Disable CSRF, again everything is entirely local
        .and().csrf().disable()

        // Enable the oAuth2 Client
        // We specify the accessTokenResponseClient only so that in DEBUG mode
        // we can inject the more-verbose Apache HttpComponentsClientHttpRequestFactory
        // otherwise it isn't strictly necessary.
        .oauth2Client(client -> client // specifying the client config isn't strictly necessary
          .authorizationCodeGrant(codeGrant -> codeGrant
            .accessTokenResponseClient(this.accessTokenResponseClient())
          )
        );

  }

  private RestTemplate restTemplate() {

    // Provide the necessary HttpMessageConverters and error handlers for the Oauth client. See:
    // https://docs.spring.io/spring-security/site/docs/5.4.5/reference/html5/#customizing-the-authorization-request
    RestTemplate restTemplate = new RestTemplate(Arrays.asList(
      new FormHttpMessageConverter(),
      new OAuth2AccessTokenResponseHttpMessageConverter())
    );
    restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());

    // If debug mode is enabled, use the better-resolution Apache HttpComponents RequestFactory
    if (log.isDebugEnabled()) {
      restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

    }
    return restTemplate;
  }

  private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>
      accessTokenResponseClient() {

    // Create the default client, and set its HttpMessageConverters ("RestOperations")
    DefaultAuthorizationCodeTokenResponseClient client =
        new DefaultAuthorizationCodeTokenResponseClient();
    client.setRestOperations(restTemplate());

    return client;
  }

}
