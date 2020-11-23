FROM openjdk:8-jdk
ARG BUILD_VERSION=v4.0.0
ENV BUILD_VERSION $BUILD_VERSION
LABEL Maintainer="bartlomiej.kolodziejczyk@j-labs.pl"
LABEL VERSION=$BUILD_VERSION
LABEL Vendor="J-Labs"
LABEL Description="Basic native android builder image"
# libs
RUN apt-get -yq update
RUN apt-get -yq install --allow-unauthenticated lib32stdc++6 lib32z1 p7zip-full
# gradle download
ARG GRADLE_BIN_VERSION=6.3
ARG GRADLE_BASE_URL=https://services.gradle.org/distributions
ARG GRADLE_SHA=038794feef1f4745c6347107b6726279d1c824f3fc634b60f86ace1e9fbd1768
RUN mkdir -p /usr/share/gradle /usr/share/gradle/ref \
  && echo "Downloading gradle" \
  && curl -fsSL -o /tmp/gradle.zip ${GRADLE_BASE_URL}/gradle-${GRADLE_BIN_VERSION}-bin.zip \
  \
  && echo "Checking download hash" \
  && echo "${GRADLE_SHA}  /tmp/gradle.zip" | sha256sum -c - \
  \
  && echo "Unziping gradle" \
  && unzip -q -d /usr/share/gradle /tmp/gradle.zip \
  \
  && echo "Cleaning and setting links" \
  && rm -f /tmp/gradle.zip \
  && ln -s /usr/share/gradle/gradle-${GRADLE_BIN_VERSION} /usr/bin/gradle
ENV GRADLE_VERSION $GRADLE_BIN_VERSION
ENV GRADLE_HOME /usr/bin/gradle
ENV GRADLE_USER_HOME /cache
ENV PATH $PATH:$GRADLE_HOME/bin
# android sdk download
ARG ANDROID_TOOLS_URL=https://dl.google.com/android/repository/commandlinetools-linux-6200805_latest.zip
RUN mkdir -p /usr/share/android \
  && echo "Downloading android linux sdk" \
  && curl -fsSL -o /tmp/android.zip ${ANDROID_TOOLS_URL} \
  \
  && echo "Unziping android sdk" \
  && unzip -q -d /usr/share/android /tmp/android.zip \
  \
  && echo "Cleaning and setting links" \
  && rm -f /tmp/android.zip \
  && ln -s /usr/share/android /usr/bin/android
RUN mkdir -p /usr/share/android/cmdline-tools
RUN mv /usr/share/android/tools /usr/share/android/cmdline-tools/tools
ENV ANDROID_HOME /usr/bin/android
ENV PATH $PATH:$ANDROID_HOME/tools:$ANDROID_HOME/tools/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/tools:$ANDROID_HOME/cmdline-tools/tools/bin
# android dependencies grab
RUN yes | sdkmanager --licenses
RUN sdkmanager "build-tools;29.0.3" "platforms;android-29" "platform-tools"
# prepare global gradle properties 
RUN mkdir -p $GRADLE_USER_HOME
RUN printf "org.gradle.parallel=true\norg.gradle.caching=true\norg.gradle.jvmargs=-Xmx12G\norg.gradle.daemon=true" > $GRADLE_USER_HOME/gradle.properties
RUN printf "org.gradle.parallel=true\norg.gradle.caching=true\norg.gradle.jvmargs=-Xmx12G\norg.gradle.daemon=true" > $GRADLE_HOME/gradle.properties
VOLUME $GRADLE_USER_HOME

