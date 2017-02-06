#MACROS
FILENAME = displayRawVideo
ARGS1 = Image1.rgb 352 288
ARGS2 = prison_176_144.rgb 176 144 
ARGS3 = st_960_540.rgb 960 540 10
ARGS4 = prison_960_540.rgb 960 540 10

# Compilation

all: clean
	javac $(FILENAME).java
	java $(FILENAME) $(ARGS3)


clean: 
	rm -rf *.class

test: clean
	javac $(FILENAME).java
	java $(FILENAME) $(ARGS2)

default: clean
	javac imageReader.java
	java imageReader $(ARGS1)
