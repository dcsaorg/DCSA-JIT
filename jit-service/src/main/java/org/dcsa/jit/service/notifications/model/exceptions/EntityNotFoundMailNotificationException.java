package org.dcsa.jit.service.notifications.model.exceptions;

public class EntityNotFoundMailNotificationException extends NonRecoverableMailNotificationException {
  public EntityNotFoundMailNotificationException(String message) {
    super(message);
  }
}
