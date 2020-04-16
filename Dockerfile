FROM verification_android_gradle_build:latest

WORKDIR .
COPY . .

RUN gradle test
