package com.storeshop.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
/**
 * Entry controller for the custom login page.
 *
 * <p>Authentication itself is handled by Spring Security; this class only serves the HTML form.
 */
public class LoginController {

  /**
   * Displays the login form page.
   *
   * @return login template name
   */
  @GetMapping("/login")
  public String login() {
    return "login/login";
  }
}
