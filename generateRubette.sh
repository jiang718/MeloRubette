rm -f melo/*.class melo.jar
javac -classpath ../rubato.jar melo/*.java
jar cvmf MeloManifest melo.jar melo
