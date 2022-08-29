package org.dcsa.jit.service.notifications.model.exceptions;

public class NonRecoverableMailNotificationException extends RuntimeException {
  public NonRecoverableMailNotificationException(String message) {
    super(message);
  }
}
