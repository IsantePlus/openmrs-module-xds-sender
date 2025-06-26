package org.openmrs.module.xdssender.notificationspullpoint;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtensibleDocumented", namespace = "http://docs.oasis-open.org/wsn/t-1", propOrder = {"documentation"})
@XmlSeeAlso({TopicSetType.class, TopicNamespaceType.class, TopicType.class})
public abstract class ExtensibleDocumented
{
  protected Documentation documentation;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap<>();

  
  public Documentation getDocumentation() {
    return this.documentation;
  }

  
  public void setDocumentation(Documentation value) {
    this.documentation = value;
  }

  
  public Map<QName, String> getOtherAttributes() {
    return this.otherAttributes;
  }
}
