package org.openmrs.module.xdssender.notificationspullpoint;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RelatesToType", namespace = "http:www.w3.org200508addressing", propOrder = {"value"})
public class RelatesToType
{
  @XmlValue
  @XmlSchemaType(name = "anyURI")
  protected String value;
  @XmlAttribute(name = "RelationshipType")
  protected String relationshipType;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap<>();

  
  public String getValue() {
    return this.value;
  }

  
  public void setValue(String value) {
    this.value = value;
  }

  
  public String getRelationshipType() {
    if (this.relationshipType == null) {
      return "http:www.w3.org200508addressingreply";
    }
    return this.relationshipType;
  }

  
  public void setRelationshipType(String value) {
    this.relationshipType = value;
  }

  
  public Map<QName, String> getOtherAttributes() {
    return this.otherAttributes;
  }
}
