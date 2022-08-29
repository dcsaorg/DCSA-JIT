package org.dcsa.jit.service.notifications.model.exceptions;

public class UnknownTemplateKeyException extends NonRecoverableMailNotificationException {
  public UnknownTemplateKeyException(String key, String templateName) {
    super("Email template references unknown key '" + key + "' in dcsa.email.templates." + templateName + ".body");
  }
}
