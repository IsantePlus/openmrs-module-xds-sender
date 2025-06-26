package org.openmrs.module.xdssender.notificationspullpoint;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TopicSetType", namespace = "http://docs.oasis-open.org/wsn/t-1", propOrder = {"any"})
public class TopicSetType
  extends ExtensibleDocumented
{
  @XmlAnyElement(lax = true)
  protected List<Object> any;
  
  public List<Object> getAny() {
    if (this.any == null) {
      this.any = new ArrayList();
    }
    return this.any;
  }
}
