# Gnirehtet Restart

App to restart [Gnirehtet](https://github.com/Genymobile/gnirehtet) from the phone.

Unless you grant this permission, it won’t work:
```
$ adb shell pm grant com.example.gnirehtetrestart android.permission.WRITE_SECURE_SETTINGS
``` 

If the connection still doesn’t work afterwards, try to force-stop Gnirehtet in the settings
and do another restart.

## Build
1. Put your keystore `output.jks` in the `app/` folder.
2. Put the properties `keystore-store-password`, `keystore-key-alias`, and `keystore-key-password` in the gradle.properties file.

Finally run
```
./gradlew assembleRelease
```
and you should be able to find the APK at `app/build/outputs/apk/release/app-release.apk`.
