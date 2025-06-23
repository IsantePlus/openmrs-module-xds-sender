package org.openmrs.module.xdssender.notificationspullpoint;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AttributedAnyType", namespace = "http:www.w3.org200508addressing", propOrder = {"any"})
public class AttributedAnyType
{
  @XmlAnyElement(lax = true)
  protected Object any;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap<>();

  
  public Object getAny() {
    return this.any;
  }

  
  public void setAny(Object value) {
    this.any = value;
  }

  
  public Map<QName, String> getOtherAttributes() {
    return this.otherAttributes;
  }
}
