# Duitsland Nieuws

[![Build Status](https://travis-ci.org/nxtstep/duitsland-nieuws-kotlin.svg?branch=develop)](https://travis-ci.org/nxtstep/duitsland-nieuws-kotlin)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)
[![codecov](https://codecov.io/gh/nxtstep/duitsland-nieuws-kotlin/branch/master/graph/badge.svg)](https://codecov.io/gh/nxtstep/duitsland-nieuws-kotlin)

App
---

Demo app to demonstrate Android + Kotlin skills.  
The content for this app is taken from a WP-JSON REST API that is publicly available.  

### Screens

![App screen](./screen.png)  ![Article screen](./screen2.png)  

#### Debug
`./gradlew assembleDebug`

#### Release
`./release.sh`

#### Checkstyle  
`./gradlew checkstyle`

#### Outdated dependecies
`./gradlew dependencyUpdates -Drevision=release`
