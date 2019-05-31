# MeloRubette
A plugin(rubette) for open source composer software rubato<br />

1. Make sure "maven" is installed in your system.
https://maven.apache.org/

2. Go into the rubato installation folder, run
    ```
    git clone https://github.com/jiang718/MeloRubette 
    cd <PATH_TO_REPO>
    ./env_config.sh
    ```

3. Instructions<br />
    To build the plugin:
    ```
    mvn package
    ```
    
    
    To interactively test the plugin
    ```
    mvn verify 
    ```
    
    To only run rubato if you don't want to rebuild:
    ```
    ./runRubato.sh
    ```
    
<br />
Have fun with demo projects: <br />
Hit "File-Open" in the right corner of the task bar, and search for projects under <PATH_TO_REPO>/demo.<br />
Please see http://www.rubato.org/ for more information.<br />
Also, you can get the latest rubato software and plugins there.<br />
<br />

    To clean:
    ```
    mvn clean
    ```
    
    To aggresively clean the plugin (this will delete melo rubette in rubato plugin folder!!!):
    ```
    mvn post-clean
    ```

<br />
<br />
---
    NOTE: rubato.jar is the rubato software, it's update to date for the time being.
    You can use the rubato.jar downloaded from the offical website http://www.rubato.org/.
    Please manually update the rubato jar if you have to.
    
    ```
    rm <PATH_TO_REPO>/lib/rubato.jar
    ln -f <PATH_TO_PREFERRED_RUBATO_JAR> <PATH_TO_REPO>/lib/rubato.jar
    ./env_config.sh
    ```
