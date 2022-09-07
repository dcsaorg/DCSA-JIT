package org.dcsa.jit.notifications.model.exceptions;

public class NonRecoverableMailNotificationException extends RuntimeException {
  public NonRecoverableMailNotificationException(String message) {
    super(message);
  }
}
