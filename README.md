# Duitsland Nieuws
*io.supersimple.duitslandnieuws*  

Demo app to demonstrate Android + Kotlin skills.  
The content for this app is taken from a WP-JSON REST API that is publically available.  

### Screens

![App screen](./screen.png)  ![Article screen](./screen2.png)

### Build

Develop|Master
-------|------
[![BuddyBuild](https://dashboard.buddybuild.com/api/statusImage?appID=5893adeb7ccc8101008da233&branch=develop&build=latest)](https://dashboard.buddybuild.com/apps/5893adeb7ccc8101008da233/build/latest?branch=develop) | [![BuddyBuild](https://dashboard.buddybuild.com/api/statusImage?appID=5893adeb7ccc8101008da233&branch=master&build=latest)](https://dashboard.buddybuild.com/apps/5893adeb7ccc8101008da233/build/latest?branch=master)

#### Debug
`./gradlew assembleDebug`

#### Release
`./release.sh`

#### Checkstyle  
`./gradlew checkstyle`

#### Outdated dependecies
`./gradlew dependencyUpdates -Drevision=release`
