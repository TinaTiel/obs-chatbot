package com.tinatiel.obschatbot.core.client.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SystemPrincipalOAuth2AuthorizedClientRepository implements OAuth2AuthorizedClientRepository {

    private final String SYSTEM_PRINCIPAL_NAME = "SYSTEM";
    private final OAuth2AuthorizedClientService authorizedClientService;

    public SystemPrincipalOAuth2AuthorizedClientRepository(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, Authentication authentication, HttpServletRequest httpServletRequest) {
        return authorizedClientService.loadAuthorizedClient(clientRegistrationId, SYSTEM_PRINCIPAL_NAME);
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient oAuth2AuthorizedClient, Authentication authentication, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        authorizedClientService.saveAuthorizedClient(
                oAuth2AuthorizedClient,
                new PreAuthenticatedAuthenticationToken(SYSTEM_PRINCIPAL_NAME, new HashMap<>(), new ArrayList<>())
        );
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, Authentication authentication, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        authorizedClientService.removeAuthorizedClient(clientRegistrationId, SYSTEM_PRINCIPAL_NAME);
    }
}
