rm -f melo/MeloSubMotifRubette.class melo/Note.class melo.jar
rm /home/nil/.rubato/plugins/melo.jar
javac -classpath ../rubato.jar melo/MeloSubMotifRubette.java melo/Note.java
jar cvmf MeloManifest melo.jar melo 
cp melo.jar /home/nil/.rubato/plugins/
java -Djava.library.path=../ -jar ../rubato.jar
