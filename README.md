 # Overview

Java/React full-stack app, analyzes Spotify listening history. MVC design pattern. Managed with maven, utilizing Spring Boot, JGit, REST architecture, the Spotify API, and build tested with Docker.

## Description

**Data-fy** is a full-stack web application designed to analyze a user's Spotify listening history and present it in engaging and informative ways. The backend interfaces with Spotify via API calls, processes the returned data, and subsequently provides the processed data to the front end through RESTful routes.

At its core, the backend leverages Java and adheres to the MVC design pattern. All pertinent data is collated into a single object, SpotifyData, serving as the model. This object encapsulates all necessary information, making it readily available for the frontend, which acts as the view.

The backend is structured around three primary controllers:

* Auth Controller: Manages the reception, verification, and local session storage of Spotify login credentials.

* Data Controller: Accesses the stored session login credentials to make requests to the Spotify API. It fetches the requisite data and transforms it into insightful and intriguing content for the user. To enhance performance, especially with larger datasets, this controller employs multi-threading, allowing simultaneous processing of multiple data segments.

* Global Exception Controller: Provides a centralized error handling mechanism. It captures exceptions across all controllers and conveys the associated error messages to the frontend, ensuring the user remains informed.

## Getting Started

### Dependencies

* Maven 4.0.0 (or suitable build automation tool of your choice)
* Java 17
* Spring Framework
    * spring-boot-starter-data-rest
    * spring-boot-devtools
    * spring-boot-starter-test
* JGit 5.1.3
* Spotify Web API Java 7.3.0
* Spotify Developer Credentials
* httpclient 4.5.14
* dotenv-java 2.3.2

### Installing

* Fork or clone this repository
* npm start for front end
* run java for server

## Author

[Josh Valentino](https://joshvalentino.com)  

## Version History

* 1.0.0
    * Base release version
* 1.1.0
    * Current WIP
