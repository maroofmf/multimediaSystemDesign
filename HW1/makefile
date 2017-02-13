#MACROS
PROGRAM = displayRawVideo
FPS = 10
WIDTH = 176
HEIGHT = 144
INPUTFILE = data/prison_176_144.rgb

# Arguments:
# Format ==> inputFilePath input_width input_height <frameRate[Default = 10]>
ARGS1 = data/prison_176_144.rgb
ARGS2 = data/prison_960_540.rgb 
ARGS3 = data/st_176_144.rgb
ARGS4 = data/st_960_540.rgb

# Compilation
all: setup
	java $(PROGRAM) $(ARGS1) 176 144 10
	java $(PROGRAM) $(ARGS2) 960 540 10
	java $(PROGRAM) $(ARGS3) 176 144 10
	java $(PROGRAM) $(ARGS4) 960 540 10
	java convertFormat $(ARGS2) out1.rgb HD2SD 0
	java convertFormat $(ARGS2) out2.rgb HD2SD 1
	java convertFormat $(ARGS1) out3.rgb SD2HD 0
	java convertFormat $(ARGS1) out4.rgb SD2HD 1
	java displayRawVideo output/out1.rgb 176 144 10
	java displayRawVideo output/out2.rgb 176 144 10
	java displayRawVideo output/out3.rgb 960 540 10
	java displayRawVideo output/out4.rgb 960 540 10
	java convertFormat $(ARGS4) out5.rgb HD2SD 0
	java convertFormat $(ARGS4) out6.rgb HD2SD 1
	java convertFormat $(ARGS3) out7.rgb SD2HD 0
	java convertFormat $(ARGS3) out8.rgb SD2HD 1
	java displayRawVideo output/out5.rgb 176 144 10
	java displayRawVideo output/out6.rgb 176 144 10
	java displayRawVideo output/out7.rgb 960 540 10
	java displayRawVideo output/out8.rgb 960 540 10

setup: clean compileOnly 
	mkdir output

clean: 
	rm -rf output
	rm -rf *.class *.rgb

custom:clean compileOnly
	@java $(PROGRAM) $(INPUTFILE) $(WIDTH) $(HEIGHT) $(FPS)

# Testing code
test: setup
	java convertFormat $(ARGS2) out1.rgb HD2SD 0
	java convertFormat $(ARGS2) out2.rgb HD2SD 1
	java convertFormat $(ARGS1) out3.rgb SD2HD 0
	java convertFormat $(ARGS1) out4.rgb SD2HD 1
	java displayRawVideo output/out1.rgb 176 144 10
	java displayRawVideo output/out2.rgb 176 144 10
	java displayRawVideo output/out3.rgb 960 540 10
	java displayRawVideo output/out4.rgb 960 540 10


# Compile only mode
compileOnly:
	javac $(PROGRAM).java
	javac videoData.java
	javac convertFormat.java
