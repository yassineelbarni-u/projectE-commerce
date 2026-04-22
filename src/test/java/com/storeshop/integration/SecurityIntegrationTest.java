package com.storeshop.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.storeshop.entities.Role;
import org.junit.jupiter.api.Test;

/**
 * Type : Test d'Intégration
 */
@org.junit.jupiter.api.Tag("Integration")
class SecurityIntegrationTest extends IntegrationTestBase {

  @Test
  void publicCatalogIsAccessibleWithoutAuthentication() throws Exception {
    mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("public/home"));
  }

  @Test
  void adminDashboardRedirectsAnonymousUsersToLogin() throws Exception {
    mockMvc.perform(get("/admin/dashboard")).andExpect(status().is3xxRedirection());
  }

  @Test
  void adminDashboardIsForbiddenToClientUsers() throws Exception {
    createUser("client-security", "secret123", Role.CLIENT);

    mockMvc
        .perform(get("/admin/dashboard").with(user("client-security").roles("CLIENT")))
        .andExpect(status().isForbidden());
  }

  @Test
  void adminLoginRedirectsToDashboard() throws Exception {
    createUser("admin-security", "secret123", Role.ADMIN);

    mockMvc
        .perform(formLogin("/login").user("admin-security").password("secret123"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/admin/dashboard"));
  }

  @Test
  void clientLoginRedirectsToHomePage() throws Exception {
    createUser("client-login", "secret123", Role.CLIENT);

    mockMvc
        .perform(formLogin("/login").user("client-login").password("secret123"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
  }
}
