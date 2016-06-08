This is a dumb little Android timer for people who take too long to make their
move in boardgames.  It uses Ryan's name & face because he's good-natured,
ABSOLUTELY NOT because he's the only person around here who sometimes needs a
shame timer.

BUILDING

I think you have to open this in Android Studio; it, uhh, "does a bunch of
stuff," after which you can either build it in Android Studio, or with:

  $ ./gradlew build
  $ adb install app/build/outputs/apk/app-debug.apk
