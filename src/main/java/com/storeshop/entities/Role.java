package com.storeshop.entities;

/**
 * Authorization roles used by Spring Security.
 *
 * <p>Values are mapped to authorities such as {@code ROLE_ADMIN} by the user-details adapter.
 */
public enum Role {
  USER,
  CLIENT,
  ADMIN
}
