package com.tinatiel.obschatbot.security;

import com.tinatiel.obschatbot.security.owner.OwnerDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * An implementation of ${@link OAuth2AuthorizedClientRepository} that always saves an ${@link
 * OAuth2AuthorizedClient} against the System user. By contrast, default implementations will
 * delegate anonymous requests (no Principle/Authorization present) to a specified repository.
 */
public class SystemPrincipalOauth2AuthorizedClientRepository implements
    OAuth2AuthorizedClientRepository {

  private final OwnerService ownerService;
  private final OAuth2AuthorizedClientService authorizedClientService;

  public SystemPrincipalOauth2AuthorizedClientRepository(
    OwnerService ownerService,
    OAuth2AuthorizedClientService authorizedClientService) {
    this.ownerService = ownerService;
    this.authorizedClientService = authorizedClientService;
  }

  @Override
  public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(
      String clientRegistrationId,
      Authentication authentication,
      HttpServletRequest httpServletRequest) {
    return authorizedClientService
      .loadAuthorizedClient(clientRegistrationId, ownerService.getOwner().getName());
  }

  @Override
  public void saveAuthorizedClient(
      OAuth2AuthorizedClient authorizedClient,
      Authentication authentication,
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse) {
    OwnerDto owner = ownerService.getOwner();
    authorizedClientService.saveAuthorizedClient(
        authorizedClient,
        new PreAuthenticatedAuthenticationToken(
          owner.getName(),
          new HashMap<>(),
          new ArrayList<>()
        )
    );
  }

  @Override
  public void removeAuthorizedClient(
      String clientRegistrationId,
      Authentication authentication,
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse) {
    authorizedClientService
      .removeAuthorizedClient(clientRegistrationId, ownerService.getOwner().getName());
  }
}
