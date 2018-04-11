rm -f melo/*.class melo.jar
rm /home/nil/.rubato/plugins/melo.jar
javac -classpath ../rubato.jar melo/*.java
jar cvmf MeloManifest melo.jar melo 
cp melo.jar /home/nil/.rubato/plugins/
rm melo/*.class
java -Djava.library.path=../ -jar ../rubato.jar
