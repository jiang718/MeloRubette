rm ~/.rubato/plugins/melo.jar
mvn package
cp target/melo-1.0.jar ~/.rubato/plugins/melo.jar
rm target/melo-1.0.jar
java -Djava.library.path=../ -jar ../rubato.jar
