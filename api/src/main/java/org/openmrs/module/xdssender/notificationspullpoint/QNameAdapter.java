package org.openmrs.module.xdssender.notificationspullpoint;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.QName;

public class QNameAdapter extends XmlAdapter<String, QName> {

    @Override
    public QName unmarshal(String v) throws Exception {
        if (v == null) {
            return null;
        }
        return QName.valueOf(v);
    }

    @Override
    public String marshal(QName v) throws Exception {
        if (v == null) {
            return null;
        }
        return v.toString();
    }
}
