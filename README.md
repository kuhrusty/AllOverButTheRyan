This is also available as a free download in the Google Play store [here](https://play.google.com/store/apps/details?id=com.kuhrusty.ryan).

This is a dumb little Android timer for people who take too long to make their
move in boardgames.  It uses Ryan's name & face because he's good-natured,
ABSOLUTELY NOT because he's the only person around here who sometimes needs a
shame timer.

It also has a sand-timer mode, where tapping on the screen makes the timer
count back _up_ (so if there were 19 seconds remaining on a 60-second timer, it
will continue counting down with 41 seconds remaining).

### INTERESTING STUFF IN THE CODE

- setting the default volume control stream (`TimerActivity.java`)
- keeping the screen on while running (`TimerActivity.java`)
- working around `CountDownTimer` strangeness (`TimerActivity.java`)
- handling different types of preferences (`SettingsActivity.java`)

### TO CHECK OUT THE CODE

In Android Studio 3.1.3:

1. File -> New -> Project from Version Control -> GitHub

2. Clone Repository:
   - Repository URL: https://github.com/kuhrusty/AllOverButTheRyan.git
   - Parent Directory: (whatever you want; probably
     /home/.../AndroidStudioProjects)
   - Directory Name: (whatever you want; probably AllOverButTheRyan)

   That should check the stuff out and start a Gradle build.  (If you
   get errors about missing .iml files, ignore them--do *not* remove the
   modules it's talking about.)

3. Hit the "Sync Project with Gradle Files" button in the toolbar.  This
   should generate the .iml files it was complaining about.

After that, you can either build it in Android Studio, or with:

    $ ./gradlew build
    $ adb install app/build/outputs/apk/app-debug.apk
