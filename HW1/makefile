#MACROS
PROGRAM = displayRawVideo
FPS = 10
WIDTH = 176
HEIGHT = 144
INPUTFILE = data/prison_176_144.rgb

# Arguments:
# Format ==> inputFilePath input_width input_height <frameRate[Default = 10]>
ARGS1 = data/prison_176_144.rgb 176 144 10
ARGS2 = data/prison_960_540.rgb 960 540 10
ARGS3 = data/st_176_144.rgb 176 144 10
ARGS4 = data/st_960_540.rgb 960 540 10

# Compilation
all: setup
	java $(PROGRAM) $(ARGS1)
	java $(PROGRAM) $(ARGS2)
	java $(PROGRAM) $(ARGS3)
	java $(PROGRAM) $(ARGS4)

setup: clean compileOnly 
	mkdir output

clean: 
	rm -rf output
	rm -rf *.class *.rgb

custom:clean compileOnly
	@java $(PROGRAM) $(INPUTFILE) $(WIDTH) $(HEIGHT) $(FPS)

# Testing code
test: videoData displayRawVideo
	javac convertFormat.java
	java convertFormat data/prison_960_540.rgb out.rgb HD2SD 1
	java displayRawVideo out.rgb $(WIDTH) $(HEIGHT) $(FPS)

# Compile only mode
compileOnly:
	javac $(PROGRAM).java
	javac videoData.java
	javac convertFormat.java
