Software Systems Security 2015
Kati Kyllönen 011539913
Assignment 4 - Android Key Store

The application work in one Activity called MyMessage 
 - it has two editable textfields and two buttons and a text field for printing results of operations
 - first text field is for entering a message and pressing the sign button - this signs the message
 - second text field is for verifying if some message is signed 
 - the results of the operations signed / verified or not verified are printed to the text field

 Implementation has possibly unnecessarily lot of global variables... 
 and it saves the signature and prints it out to the screen ;)


"Benefits of a hardware-backed Android Key Store implementation. 
What are the benefits for a 3rd party application developer? 
How would your answer change if you consider the software-based implemention."

The Android Key Store implementation allows 3rd party applications to create, 
store and use their keys (and other secrets) in a secure manner via a the provided API. 
By using Key Store only the application (identified by uid) that created the key can access it.
Depending of the device the Key Store implementation can be hardware-backed or based on software.
In a hardware implementation keys are generated outside the Android operating system and are not 
directly accessible by the system or the root user. So the hardware implementation ensures well that
only the application has access to it's own keys. This can of course vary depending on the device 
hardware implementation. In software implementation the keys are encrypted with a user specific 
encryption key. Which is as secure as the encryption is. The software implementation is the fall-back 
mode if the device does not support hardware-backed Key Store.

Reference: http://nelenkov.blogspot.co.il/2013/08/credential-storage-enhancements-android-43.html




