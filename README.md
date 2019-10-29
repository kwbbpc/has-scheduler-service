
API Documentation can be found at 
http://localhost:9000/swagger-ui.html when running this application locally.


This program will run as an embedded tomcat application built on Spring.  It provides a REST API to clients 
to allow web browsers to set and make schedules for watering commands.  Watering commands are packaged up into
protobuf format and sent to SQS for delivery to the listening XBEE device.  XBEE commands sent to the device are
to be delivered with an expiration timestamp.  The expiration timestamp is the time at which the command/message 
is no longer valid, and if recieved after this time, should be disregaurded and logged as a failure to execute.
 
 
 Days are requested for scheduling by day of week, corresponding to:
 @ApiModelProperty(notes = "MONDAY = 1" +
             "\n TUESDAY = 2" +
             "\n WEDNESDAY = 3" +
             "\n THURSDAY = 4" +
             "\n FRIDAY = 5" +
             "\n SATURDAY = 6" +
             "\n SUNDAY = 7")