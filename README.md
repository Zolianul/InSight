InSight is an integrated system designed to enhance security and user interaction through the combination of a mobile application and a Raspberry Pi-based facial recognition system. The project encompasses two primary components: a mobile application developed in Android studio with Java and a web-based control system utilizing Python and HTML, hosted on a Raspberry Pi. The mobile application facilitates user management tasks such as registration, login, profile updates, and image handling, leveraging Firebase for backend services. Concurrently, the Raspberry Pi system captures real-time video, processes facial recognition, and provides motor control for viewing different angles of the video stream.

This project aims to create a seamless user experience by enabling users to register, authenticate, and manage their profiles through the mobile app while utilizing the Raspberry Pi system for real-time surveillance and control.



Following, are instructions to run this app:

It is necessary to download the source code and import it into AndroidStudio - this IDE is available for free.

After all the packages and resources used in the application are downloaded by AndroidStudio, you can run the application through the interface of this IDE (there is a "RUN" button in the top right corner).

The waiting time until the application is compiled and starts running can vary from 1 to 3 minutes. The first run generally takes 5 minutes.


In order to view the live stream sent from the RaspberryPi, some changes need to be made:

a) Ensure that the RaspberryPi and the device from where the app is run, are on the same network ( same IP address)

b) In the StreamView.java class, replace the ip address from : "mWebView.loadUrl("http://xxx.xxx.xxx.xxx:5000");" with your own IP address. This IP will need to mach the one you will supply to the RaspberryPi, in the main.py script.

