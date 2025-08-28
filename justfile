#!/usr/bin/env just --justfile

build:
    ./gradlew -x test build

test:
    ./gradlew build

jib:
    ./gradlew clean build jibDockerBuild -i

up:
    docker compose --env-file gradle.properties up -d --scale event-sourcing-app=2

down:
    docker compose down -v

e2e:
    E2E_TESTING=true ./gradlew clean test -i

test-e2e: jib up e2e
    just down
