# LostFinderServer
## Table of contents 
* [General info](#general-info)
* [Technologies](#technologies)
* [Used external services](#used-external-services)
* [Setup](#setup)

# General info
This project is a server for Android application LostFinder which code can be found on:
https://github.com/mmodzel3/LostFinder

Mobile application can be used to help searching of missing people.
Participants of searching can see location of each other on map.
Alerts about important situations can be sent. It generates notifications to logged users using Firebase Cloud Messaging. 
Marker with location is also visible on map.
During searching chat is available for communication.
What is more, actual weather data can be downloaded from OpenWeatherMap: https://openweathermap.org/ and can be seen on mobile device.
Application distinguishes three roles: User, Manager and Owner.
With appropriate permissions users' accounts can be managed from application.

# Technologies
* Java Spring
* Gradle
* JUnit
* MongoDB

# Used external services
* Firebase Cloud Messaging

# Setup
Before compiling project to run FCM (Firebase Cloud Messaging) notifications "firebase-service-account.json" 
has to be added to "src/main/resources" directory. 
It should contain private authorization keys for managing notifications by server.
It can be generated on: firebase.google.com/cloud/messaging

Server configuration can be found in "src/main/resources/application.properties".
Import to change are:
* server.port - server port
* spring.data.mongodb.uri - MongoDB database URI
* jwt.secret - secret password used to encrypt generated authentication tokens
* register.server.password - password used to protect account registration from unauthorized users - if blank no protection
* notification.firebase.config.file - FCM (Firebase Cloud Messaging) authorization private key config file
