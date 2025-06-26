package org.openmrs.module.xdssender.notificationspullpoint;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProblemActionType", namespace = "http://www.w3.org/200508/addressing", propOrder = {"action", "soapAction"})
public class ProblemActionType
{
  @XmlElement(name = "Action")
  protected AttributedURIType action;
  @XmlElement(name = "SoapAction")
  @XmlSchemaType(name = "anyURI")
  protected String soapAction;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap<>();

  
  public AttributedURIType getAction() {
    return this.action;
  }

  
  public void setAction(AttributedURIType value) {
    this.action = value;
  }

  
  public String getSoapAction() {
    return this.soapAction;
  }

  
  public void setSoapAction(String value) {
    this.soapAction = value;
  }

  
  public Map<QName, String> getOtherAttributes() {
    return this.otherAttributes;
  }
}
