# XDS Sender Module
![CI](https://github.com/pmanko/openmrs-module-xds-sender/workflows/CI/badge.svg)

The XDS Sender module pushes standardized Continuity of Care Documents (CCD) from OpenMRS to a third party XDS repository like the OpenXDS/OpenSHR shared health record. There is a listener that listens for encounter events. Each time an encounter is created or saved, this module creates a CCD and attempts to push it to the third party system. Failed pushes raise an ActiveMQ event that can be used to trigger subsequent retries through other mechanisms.

## Module Setup

This module requires that tomcat have permissions to write logs to a folder called xdslogs. This is dependent on your log environment. If tomcat was installed with apt-get, you will need to create a folder in /var/log/ and give the tomcat user permissions

```
sudo mkdir /var/log/xdslog
sudo chown tomcat7:tomcat7 /var/log/xdslog
```

## License

[MPL 2.0 w/ HD](http://openmrs.org/license/)
