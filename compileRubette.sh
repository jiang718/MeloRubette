rm -f melo/*.class melo.jar
rm /home/nil/.rubato/plugins/melo.jar
javac -classpath ../rubato.jar melo/MeloRubette.java melo/Note.java melo/Score.java melo/MotifManager.java
