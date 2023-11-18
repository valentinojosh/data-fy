 # Overview

Java/React full-stack app, analyzes Spotify listening history. MVC design pattern. Managed with maven, utilizing Spring Boot, JGit, REST architecture, the Spotify API, and build tested with Docker.

[![Netlify Status](https://api.netlify.com/api/v1/badges/1f537465-137b-44a5-860e-50a5a3cbad36/deploy-status)](https://app.netlify.com/sites/data-fy/deploys)
[![button](https://github.com/buttons/github-buttons/workflows/build/badge.svg)](https://data-fy.netlify.app/)

## Description

**Data-fy** is a full-stack web application designed to analyze a user's Spotify listening history and present it in engaging and informative ways. The backend interfaces with Spotify via API calls, processes the returned data, and subsequently provides the processed data to the front end through RESTful routes.

At its core, the backend leverages Java and adheres to the MVC design pattern. All pertinent data is collated into a single object, SpotifyData, serving as the model. This object encapsulates all necessary information, making it readily available for the frontend, which acts as the view.

The backend is structured around three primary controllers:

* Auth Controller: Manages the reception and verification of the Spotify authorization token.

* Data Controller: Utilizes the received login credentials (from the front end) to make requests to the Spotify API. It fetches the Spotify listening data and parses out the requisite data to be processed and related in meaningful ways. It is then sent to the front end to be transformed into insightful and intriguing content for the user. To enhance performance, especially with larger datasets, this controller employs multi-threading, allowing simultaneous processing of multiple data segments. In particular, getTotalMinutes, the function that is responsible for calculating the estimation of total minutes listened in the last year, can be extensive due to it being recursive. This function is designed as such becasue of the Spotify API limit of 50 tracks per call. Thus the function is recusviley called, each time requesting the next 50 tracks unill it reaches either one full week of listening, or the max the user's history goes.

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

### Commands

* docker build --no-cache -t image-name .
* docker run \ -e "VARIABLE=value" \ -p port:port image-name
* mvn clean package -Dspring.profiles.active=production appengine:deploy

## Author

[Josh Valentino](https://joshvalentino.com)  

## Version History

* 1.0.0
    * Inital release
* 1.1.0
    * Fully fucntional app in testing env
* 1.2.0
    * Improved API routes, slightly improved security
* 1.3.0
    * Further improved API routes, FE calls simplified and secured, enhanced caching, implemented reduction in un-necesary calls to api
* 1.4.0
    * Combatibility with Google App Engine, Secrets Manager, etc.
    * First production build
 * 1.5.0
    * Refactored project to be completely stateless on the backend
    * Added more redirects and logout functionality to client side
