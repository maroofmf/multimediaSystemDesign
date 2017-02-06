#MACROS
PROGRAM = displayRawVideo
FPS = 10

# Arguments:
# Format ==> inputFilePath input_width input_height <frameRate[Default = 10]>
ARGS1 = data/prison_176_144.rgb 176 144 10
ARGS2 = data/prison_960_540.rgb 960 540 10
ARGS3 = data/st_176_144.rgb 176 144 10
ARGS4 = data/st_960_540.rgb 960 540 10

# Compilation
all: clean
	javac $(PROGRAM).java
	java $(PROGRAM) $(ARGS1)
	java $(PROGRAM) $(ARGS2)
	java $(PROGRAM) $(ARGS3)
	java $(PROGRAM) $(ARGS4)
	
clean: 
	rm -rf *.class

compile: 
	javac $(PROGRAM).java

custom:clean compile
	@java $(PROGRAM) $(INPUTFILE) $(WIDTH) $(HEIGHT) $(FPS)
