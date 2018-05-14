SnapTwitter for App Engine Standard (Java 8)
============================

Automatically delete tweets after a interval of days

See the [Google App Engine standard environment documentation][ae-docs] for more
detailed instructions.

[ae-docs]: https://cloud.google.com/appengine/docs/java/


* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Maven](https://maven.apache.org/download.cgi) (at least 3.5)
* [Gradle](https://gradle.org/gradle-download/) (optional)
* [Google Cloud SDK](https://cloud.google.com/sdk/) (aka gcloud)

## Setup

• Download and initialize the [Cloud SDK](https://cloud.google.com/sdk/)

    gcloud init

* Create an App Engine app within the current Google Cloud Project


    gcloud app create

* Edit the `twitter4j.properties` file filling up with your credentials

* Edit the **final variables** at `SnapTwitter.java` to personalize behavior   

## Maven
### Running locally

    mvn appengine:run

To use vist: 
* [http://localhost:8080/timeline?rt](http://localhost:8080/timeline?rt) to delete retweeted tweets below average
* [http://localhost:8080/timeline](http://localhost:8080/timeline) to delete the rest

### Deploying

    mvn appengine:deploy

To use vist:  https://YOUR-PROJECT-ID.appspot.com