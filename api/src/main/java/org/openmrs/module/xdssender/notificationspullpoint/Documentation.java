package org.openmrs.module.xdssender.notificationspullpoint;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Documentation", namespace = "http://docs.oasis-open.org/wsn/t-1", propOrder = {"content"})
public class Documentation
{
  @XmlMixed
  @XmlAnyElement(lax = true)
  protected List<Object> content;
  
  public List<Object> getContent() {
    if (this.content == null) {
      this.content = new ArrayList();
    }
    return this.content;
  }
}
