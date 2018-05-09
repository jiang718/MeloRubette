# MeloRubette
A plugin(rubette) for open source composer software rubato<br />

First download and install rubato software from http://www.rubato.org/rubatocomposer.html<br />
Then go into the rubato installation folder, run
```
git clone MeloRubette
cd MeloRubette
```
Copy the melo.jar into rubato's plugin folder. This might vary based on your system. Please refer to rubato documentation. <br/>
You can also try compiling source code by: <br>
```
bash generateRubette
```
This command will generated a new melo.jar based on source code in "melo" folder.
<br>
Go to the rubato installation folder, and open rubato.
Please refer to rubato documentation to see how to open it. Normally, double click to open is enough.<br/>
<br/>
Notes for Linux User:
For linux users doesn't install Leap Motion library. Please copy libLeap.so and lieLeapJava.so inside LinuxSupport into your rubato installation folder.
Run rubato inside installation folder with the following command:
```
java -Djava.library.path=./ -jar rubato.jar
```
