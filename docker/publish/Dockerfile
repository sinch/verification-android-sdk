FROM 021785113572.dkr.ecr.eu-west-1.amazonaws.com/verification_android_gradle_build:15

WORKDIR .
COPY . .

ENTRYPOINT ["gradle", ":library:bintrayUpload"]