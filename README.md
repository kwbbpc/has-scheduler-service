
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
             
             
             
  #Building the docker image
  mvn clean install dockerfile:build
  
  #Pushing the docker image
  mvn dockerfile:push
  
  
  #Releasing new versions
  Uses the maven-release plugin, see docs at https://maven.apache.org/maven-release/maven-release-plugin
  
  ##Preparing for release
  Preparing a release goes through the following release phases:
  
  Check that there are no uncommitted changes in the sources
  Check that there are no SNAPSHOT dependencies
  Change the version in the POMs from x-SNAPSHOT to a new version (you will be prompted for the versions to use)
  Transform the SCM information in the POM to include the final destination of the tag
  Run the project tests against the modified POMs to confirm everything is in working order
  Commit the modified POMs
  Tag the code in the SCM with a version name (this will be prompted for)
  Bump the version in the POMs to a new value y-SNAPSHOT (these values will also be prompted for)
  Commit the modified POMs
  
   - mvn release:prepare
   
   ##Releasing
   Performing a release runs the following release phases:
   
   - Checkout from an SCM URL with optional tag
   - Run the predefined Maven goals to release the project (by default, deploy site-deploy)
   
   To actually run the release, use
   - mvn release:perform