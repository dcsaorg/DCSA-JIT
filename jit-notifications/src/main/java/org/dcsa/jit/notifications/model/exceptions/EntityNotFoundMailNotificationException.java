package org.dcsa.jit.notifications.model.exceptions;

public class EntityNotFoundMailNotificationException extends NonRecoverableMailNotificationException {
  public EntityNotFoundMailNotificationException(String message) {
    super(message);
  }
}
