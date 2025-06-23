package org.openmrs.module.xdssender.notificationspullpoint;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"notificationMessage", "any"})
@XmlRootElement(name = "Notify")
public class Notify
{
  @XmlElement(name = "NotificationMessage", required = true)
  protected List<NotificationMessageHolderType> notificationMessage;
  @XmlAnyElement(lax = true)
  protected List<Object> any;
  
  public List<NotificationMessageHolderType> getNotificationMessage() {
    if (this.notificationMessage == null) {
      this.notificationMessage = new ArrayList<>();
    }
    return this.notificationMessage;
  }

  
  public List<Object> getAny() {
    if (this.any == null) {
      this.any = new ArrayList();
    }
    return this.any;
  }
}
