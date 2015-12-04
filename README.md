## Synopsis

Common libraries used by many NICS components


## Dependencies
- NONE

## Building

    mvn package


## Description

 - dao - tools to help query the database
 - email-alert - Library used by email-consumer for building email messages
 - encryption-lib -
 - entities - POJOs that represent NICS database entities
 - geoserver-rest-api - API to interface with geoserver
 - hash-lib -
 - message-parser - Parses rabbit messages and used with collabfeed-manager and other consumers
 - messages - Message entities being sent to the consumers
 - nics-constants
 - nics-dao - Leveraged by em-api to interface with the database
 - rabbitmq-admin-interface - Interfaces with rabbitmq to make admin updates
 - rabbitmq-client
 - webservice-request - Tools for requesting information from an endpoint or other source and formatting the results
 - xml-email - Library used by email-consumer for building email messages


## Documentation

Further documentation is available at nics-common/docs
