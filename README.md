# MeloRubette
A plugin(rubette) for open source composer software rubato<br />

First download and install rubato software from http://www.rubato.org/rubatocomposer.html<br />
Then go into the rubato installation folder, run
```
git clone https://github.com/jiang718/MeloRubette 
cd MeloRubette
```

To build the plugin:
```
mvn package
```


To interactively test the plugin
```
mvn verify 
```

To clean:
```
mvn clean
```

To aggresively clean the plugin (this will delete melo rubette in rubato plugin folder!!!):
```
mvn post-clean
```

To only run rubato:
```
./scripts/runRubato.sh
```
