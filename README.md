# MeloRubette
A plugin(rubette) for open source composer software rubato<br />

First download and install rubato software from http://www.rubato.org/rubatocomposer.html<br />
Clone this folder inside rubato folder. <br/>
Then go into the rubato installation folder, run
```
git clone MeloRubette
```
Go to the MeloRubette folder, run:
```
bash generateRubette
```
Copy the generated melo.jar into rubato's plugin folder. This might vary based on your system. Please refeter to rubato documentation. <br/>
Go to the rubato installation folder, and open rubato.
Please refer to rubato folder to see how to open it.<br/>
<br/>
Notes for Linux User:
Copy libLeap.so and lieLeapJava.so inside LinuxSupport into your rubato installation folder.
Run rubato inside installation folder with the following command:
...
java -Djava.library.path=./ -jar rubato.jar
...
