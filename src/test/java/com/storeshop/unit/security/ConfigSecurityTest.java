package com.storeshop.unit.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.storeshop.security.ConfigSecurity;
import com.storeshop.services.impl.UserDetailsServiceImpl;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@DisplayName("Tests de ConfigSecurity")
/**
 * Type : Test Unitaire
 */
@org.junit.jupiter.api.Tag("Unitaire")
class ConfigSecurityTest {

  @Test
  @DisplayName("authenticationSuccessHandler redirige un admin vers le dashboard")
  void authenticationSuccessHandlerRedirectsAdminToDashboard()
      throws ServletException, IOException {
    ConfigSecurity configSecurity = new ConfigSecurity(mock(UserDetailsServiceImpl.class));
    AuthenticationSuccessHandler successHandler = configSecurity.authenticationSuccessHandler();

    MockHttpServletResponse response = new MockHttpServletResponse();
    successHandler.onAuthenticationSuccess(
        new MockHttpServletRequest(),
        response,
        new TestingAuthenticationToken(
            "admin", "password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));

    assertEquals("/admin/dashboard", response.getRedirectedUrl());
  }

  @Test
  @DisplayName("authenticationSuccessHandler redirige un client vers l'accueil")
  void authenticationSuccessHandlerRedirectsClientToHome()
      throws ServletException, IOException {
    ConfigSecurity configSecurity = new ConfigSecurity(mock(UserDetailsServiceImpl.class));
    AuthenticationSuccessHandler successHandler = configSecurity.authenticationSuccessHandler();

    MockHttpServletResponse response = new MockHttpServletResponse();
    successHandler.onAuthenticationSuccess(
        new MockHttpServletRequest(),
        response,
        new TestingAuthenticationToken(
            "client", "password", List.of(new SimpleGrantedAuthority("ROLE_CLIENT"))));

    assertEquals("/", response.getRedirectedUrl());
  }
}
