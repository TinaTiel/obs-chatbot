package com.tinatiel.obschatbot.security;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Configure application-wide HTTP security, including enabling OAuth2 Client features.
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  /**
   * This is responsible for noting that an authorization request was made. Without a registered
   * authorization request (initiated at /oauth2/authorization/{registrationId}, the corresponding
   * security filters (e.g. OAuth2AuthorizationCodeGrantFilter) will not attempt code exchange. Even
   * if we want to use the default, we need to redeclare it here because the default bean is final
   * and cannot be mocked in tests.
   */
  @Bean
  AuthorizationRequestRepository<OAuth2AuthorizationRequest> customAuthorizationRepository() {
    return new HttpSessionOAuth2AuthorizationRequestRepository(); // the default
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        // All web requests are served and consumed on localhost
        .authorizeRequests().anyRequest().permitAll().and()

        // Disable CSRF, again everything is entirely local
        .csrf().disable()

        // Same-origin iframes (for h2-console)
        .headers().frameOptions().sameOrigin().and()

        // Enable the oAuth2 Client
        // We specify the accessTokenResponseClient only so that in DEBUG mode
        // we can inject the more-verbose Apache HttpComponentsClientHttpRequestFactory
        // otherwise it isn't strictly necessary.
        .oauth2Client(client -> client // specifying the client config isn't strictly necessary
          .authorizationCodeGrant(codeGrant -> codeGrant
            .accessTokenResponseClient(this.accessTokenResponseClient())
            .authorizationRequestRepository(this.customAuthorizationRepository())
          ));

  }

  private RestTemplate restTemplate() {

    // Provide the necessary HttpMessageConverters and error handlers for the Oauth client. See:
    // https://docs.spring.io/spring-security/site/docs/5.4.5/reference/html5/#customizing-the-authorization-request
    RestTemplate restTemplate = new RestTemplate(Arrays.asList(
      new FormHttpMessageConverter(),
      new OAuth2AccessTokenResponseHttpMessageConverter()
    ));
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
