# MeloRubette
### A plugin(rubette) for open source composer software rubato
<br />

## Dependencies
- Install [Java JDK](https://www.oracle.com/technetwork/java/javase/downloads/index.html)
- Install [maven](https://maven.apache.org/install.html).
- \(optional) Install [rubato](http://www.rubato.org/rubatocomposer.html) if you want to, you can use the one in this repository instead. See [Updating Rubato](#updating-rubato)
<br />

## Installations 
```
cd <PARENT_FOLDER_OF_YOUR_CHOICE>
git clone https://github.com/jiang718/MeloRubette 
cd MeloRubette
./env_config.sh
```
<br />

## Instructions
### To build the plugin:
**Make sure you at least build once in your system.**
```
mvn package
```
### To rebuild and test your modification in Rubato
```
mvn verify 
```
### To only run rubato without rebuilding:
```
./runRubato.sh
```
### Have Fun with Demo Projects
Hit "File-Open" in the right corner of the task bar, and search for projects under <PATH_TO_REPO>/demo.
Please see [Rubato Offical Website](http://www.rubato.org/) for more information.
Also, you can get the latest rubato software and plugins there.
### To clean:
```
mvn clean
```
### To aggresively clean the plugin (this will delete melo rubette in rubato plugin folder!!!):
```
mvn post-clean
```
<br />

## <a name="updating-rubato">UPDATING RUBATO</a>
NOTE: rubato.jar is the rubato software, it's update to date for the time being.
You can use the rubato.jar downloaded from [Offical Website](http://www.rubato.org/rubatocomposer.html) and manually update the rubato.jar.
### Download Rubato
```
cd <PATH_TO_DOWNLOAD_FOLDER>
unzip rubato-bigbang.zip
```
### \(optional) Move rubato.jar to a folder of choice
```
mv rubato-bigbang/rubato.jar <FOLDER_OF_YOUR_CHOICE>/
```
### Link MeloRubette with new rubato.jar
```
rm <PATH_TO_MELORUBETTE>/lib/rubato.jar
ln -f <FOLDER_CONTAINING_RUBATO>/rubato.jar <PATH_TO_MELORUBETTE>/lib/rubato.jar
./env_config.sh
```
