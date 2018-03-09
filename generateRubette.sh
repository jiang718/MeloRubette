rm -f MeloSubMotifRubette.class MeloSubMotifRubette.jar
javac -classpath ../rubato.jar MeloSubMotifRubette.java
jar cvmf MeloSubMotifManifest MeloSubMotifRubette.jar MeloSubMotifRubette.class
cp MeloSubMotifRubette.jar /home/nil/.rubato/plugins/
java -jar ../rubato.jar
