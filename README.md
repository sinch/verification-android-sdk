## Overview

Sinch offers a platform for phone number verification. It consists of different software development kits – the Sinch
SDKs – that you integrate with your smartphone or web application and cloud based back-end services. Together, they
enable SMS, Flashcall, Callout and Seamless verification in your application.

## Register an Application

1. Register a [Sinch Developer account](https://portal.sinch.com/#/signup)
2. Set up a new Application using the Dashboard, where you can then obtain an **Application Key**.
3. Enable Verification for the application by selecting: **Authentication** \> **Public** under **App** \> **Settings**
   \> **Verification**

## Add the Sinch library

The Sinch Verification SDK is available publicly on mavenCentral. To include it in your Android application, make sure
your **project** level build.gradle file contains:

```text
buildscript {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
}
```

Now, in your **module** level build.gradle file, you can add Sinch SDK as a dependency:

```text
dependencies {
    implementation 'com.sinch.android.sdk.verification:verification-all:*.*.*'
  }
```

If your application uses all the verification methods it's easiest to add the  _verification-all_ package. If you intend
to use only specific verification types, you may include only their dependencies.

- verification-sms
- verification-flashcall
- verification-callout
- verification-seamless

##### Example:

If your application relies only on SMS verification and doesn't use any other verificaiton methods you should simply
add:

```text
dependencies {
    implementation 'com.sinch.android.sdk.verification:verification-sms:*.*.*'
  }
```

The latest version of the SDK can be checked
on [here](https://search.maven.org/search?q=com.sinch.android.sdk.verification).

## Samples

A repository with fully functional samples is available
on [GitHub](https://github.com/sinch/verification-samples/tree/master/Android-Verification-SDK).

## Documentation

Full SDK documentation website together with code snippets and example usages can be
found [here](https://developers.sinch.com/docs/verification-for-android).