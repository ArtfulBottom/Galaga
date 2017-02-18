JC = javac
JVM = $(JAVA_HOME)/bin/java
.SUFFIXES: .java .class
.java.class: 
	$(JC) $*.java

CLASSES = \
	Beam.java \
	Boss.java \
	Controller.java \
	Enemy.java \
	Missle.java \
	Model.java \
	Player.java \
	ProjectilesThread.java \
	ReadFromFile.java \
	SpawningThread.java \
	Sprite.java \
	WriteToFile.java \
	Game.java

MAIN = Game

default: classes

classes: $(CLASSES:.java=.class)

run: classes
	$(JVM) $(MAIN) 

clean:
	$(RM) *.class
