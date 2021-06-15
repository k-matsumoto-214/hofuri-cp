package com.keisuke.hofuri.exception;

public class RegistrationFailureException extends Exception {
  private static final long serialVersionUID = 1L;

  public RegistrationFailureException(String message) {
    super(message);
  }
}
