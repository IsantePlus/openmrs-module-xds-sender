package org.openmrs.module.xdssender.notificationspullpoint;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class TopicBR2
  extends JAXBElement<TopicExpressionType>
{
  protected static final QName NAME = new QName("http:docs.oasis-open.orgwsnbr-2", "Topic");
  
  public TopicBR2(TopicExpressionType value) {
    super(NAME, TopicExpressionType.class, null, value);
  }
  
  public TopicBR2() {
    super(NAME, TopicExpressionType.class, null, null);
  }
}
