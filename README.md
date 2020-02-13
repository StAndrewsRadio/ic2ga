# Icecast to Google Analytics

Icecast to Google Analytics (aka ic2ga) is a handy bit of software that pulls information on listeners from Icecast and
uploads them to Google Analytics.

## Features
The current release includes the following features:
* Easily import Icecast statistics into Google Analytics.
* Get listener statistics from any number of servers and mountpoints.
* Log current show as obtained from Google Sheet or a custom URL.
* Log the listener's current lag when using [icecast-kh](https://github.com/karlheyes/icecast-kh).
* Upload the information repeatedly with a custom refresh rate.
* Ignores listeners who are listening from localhost.

### Planned features
There are no additional features that are planned. If you have any suggestions, please leave an issue.

## Downloading
This project requires Java 11 and higher.

### Precompiled
To download precompiled binaries, head to the releases tab.

### From Source
To compile the binary, clone the git repo and run `mvn clean package`. The JAR file will be found in the `target`
folder and will be named `ic2ga-VERSION-jar-with-dependencies.jar`.

## Usage
To use, simply run `java -jar ic2ga.jar /path/to/config.xml`. For more information about configuring ic2ga, read the
example config file. 

## Credits
This project uses [jackson-dataformat-xml](https://github.com/FasterXML/jackson-dataformat-xml) and 
[woodstox](https://github.com/FasterXML/woodstox) to process the configuration file and listener information from
Icecast.

This project was created for [St Andrews Radio](https://standrewsradio.com).