# Intro #

This is what I currently consider the ultimate logging architecture.

![http://firewood.googlecode.com/svn/trunk/logback-dream.png](http://firewood.googlecode.com/svn/trunk/logback-dream.png)

# Details #

  * all applications use an asynchronous appender to send their events to the LogServer
  * LogServer saves these events in an fast, non-transactional database (eg. H2)
  * GUI Clients can connect to LogServer and receive live events
  * use multicast between LogServer and GUI cients to reduce bandwidth usage
  * GUI Clients can filter which events they want to receive
  * GUI Clients can query for saved logging events
  * search/filter/high-light events on the client
  * same user interface for viewing live events and events retrieved from database