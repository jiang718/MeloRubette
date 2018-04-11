rm -f MeloSubMotifRubette.class Note.class melo.jar
rm /home/nil/.rubato/plugins/melo.jar
javac -classpath ../rubato.jar MeloSubMotifRubette.java Note.java
jar cvmf MeloManifest melo.jar . 
cp melo.jar /home/nil/.rubato/plugins/
java -Djava.library.path=../ -jar ../rubato.jar
