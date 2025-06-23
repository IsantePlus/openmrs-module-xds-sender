package org.openmrs.module.xdssender.notificationspullpoint;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class TopicB2
  extends JAXBElement<TopicExpressionType>
{
  protected static final QName NAME = new QName("http:docs.oasis-open.orgwsnb-2", "Topic");
  
  public TopicB2(TopicExpressionType value) {
    super(NAME, TopicExpressionType.class, null, value);
  }
  
  public TopicB2() {
    super(NAME, TopicExpressionType.class, null, null);
  }
}
