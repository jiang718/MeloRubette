MELOSUBMOTIF=MeloSubMotifRubette

all: copy 
	java -jar ../rubato.jar

copy: $(MELOSUBMOTIF).jar
	cp MeloSubMotifRubette.jar /home/nil/.rubato/plugins/

compile: $(MELOSUBMOTIF).jar

$(MELOSUBMOTIF).jar: $(MELOSUBMOTIF).class
	jar cvmf MeloSubMotifManifest MeloSubMotifRubette.jar MeloSubMotifRubette.class

$(MELOSUBMOTIF).class: $(MELOSUBMOTIF).java
	javac -classpath ../rubato.jar MeloSubMotifRubette.java


clean:
	$(RM) MeloSubMotifRubette.class MeloSubMotifRubette.jar
