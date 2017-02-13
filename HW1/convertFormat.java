import java.util.*;
import java.io.*;

public class convertFormat{
	
//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Global variables

private String inputFileName;		
private String outputFileName;		
private String conversion;		
private int antiAliasing;
private int imageWidth;
private int imageHeight;
private byte[] pixelBuffer;
private byte[] outputPixelBuffer;
private static int frameNumber = 1; 
private int totalFrames;

//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Constructor
	public convertFormat(String[] args){
		
		// Parsing command line arguments:
		if(args.length!=4){
			System.out.println("\033[0;31mWrong Usage! Correct Usage: java [program name] [InputFile] [OutputFile] [Conversion] [AntiAliasing filter]\033[0m");			
			System.exit(0);
		}

		// Initializing variables from command line input:
		inputFileName = args[0];
		outputFileName = args[1];
		conversion = args[2];
		antiAliasing = Integer.parseInt(args[3]);
	
		if(conversion.equals("SD2HD")) sd2hd();
		else if(conversion.equals("HD2SD")) hd2sd();
	}

//---------------------------------------------------------------------------------------------------------------------------------------------------//
// SD2HD conversion:
	public void sd2hd(){

		
		System.out.println("\033[0;31mConverting from SD2HD\033[0m");
		
		// Read input video
		videoData inputVideo = new videoData(176,144);
		inputVideo.readVideo(inputFileName);
		
		// Create outputVideo
		videoData outputVideo = new videoData(960,540,3*960*540*inputVideo.totalFrames);

		if(antiAliasing==1){
			
			
			for(int frameNumber = 1; frameNumber <= outputVideo.totalFrames; frameNumber++){
				for(int channel = 0; channel < 3 ; channel++){
					for(int rowIndex = 0; rowIndex < outputVideo.imageHeight; rowIndex++){
						for(int columnIndex = 0; columnIndex < outputVideo.imageWidth; columnIndex++){
							
							double inputRowIndex = (double)(((long)(inputVideo.imageHeight)*(long)rowIndex)/(long)outputVideo.imageHeight);
							
							double inputColumnIndex = (double)(((long)(inputVideo.imageWidth)*(long)columnIndex)/(long)outputVideo.imageWidth);

							outputVideo.setPixelValue(inputVideo.accessPixelValue(inputRowIndex, inputColumnIndex, channel, frameNumber),rowIndex,columnIndex,channel,frameNumber);

						}
					}

				}	
			}

			outputVideo.gaussianFiltering();

		}else{
			
			for(int frameNumber = 1; frameNumber <= outputVideo.totalFrames; frameNumber++){
				for(int channel = 0; channel < 3 ; channel++){
					for(int rowIndex = 0; rowIndex < outputVideo.imageHeight; rowIndex++){
						for(int columnIndex = 0; columnIndex < outputVideo.imageWidth; columnIndex++){
							
							int inputRowIndex = (int)(((long)(inputVideo.imageHeight)*(long)rowIndex)/(long)outputVideo.imageHeight);
							
							int inputColumnIndex = (int)(((long)(inputVideo.imageWidth)*(long)columnIndex)/(long)outputVideo.imageWidth);

							outputVideo.setPixelValue(inputVideo.accessPixelValue(inputRowIndex, inputColumnIndex, channel, frameNumber),rowIndex,columnIndex,channel,frameNumber);

						}
					}

				}	
			}
			
		
		}


		System.out.println("\033[0;32mConversion complete \033[0m");
		
		// Save video and return
		outputVideo.saveVideo("/output"+outputFileName);
		
		return;

	}


//---------------------------------------------------------------------------------------------------------------------------------------------------//
// HD2SD conversion:
	public void hd2sd(){
		

		System.out.println("\033[0;31mConverting from HD2SD\033[0m");
		
		// Read input video
		videoData inputVideo = new videoData(960,540);
		inputVideo.readVideo(inputFileName);
		
		// Create outputVideo
		videoData outputVideo = new videoData(176,144,3*176*144*inputVideo.totalFrames);

		if(antiAliasing==1){
			
			inputVideo.gaussianFiltering();			

			for(int frameNumber = 1; frameNumber <= outputVideo.totalFrames; frameNumber++){
				for(int channel = 0; channel < 3 ; channel++){
					for(int rowIndex = 0; rowIndex < outputVideo.imageHeight; rowIndex++){
						for(int columnIndex = 0; columnIndex < outputVideo.imageWidth; columnIndex++){
							
							double inputRowIndex = (double)(((long)(inputVideo.imageHeight)*(long)rowIndex)/(long)outputVideo.imageHeight);
							
							double inputColumnIndex = (double)(((long)(inputVideo.imageWidth)*(long)columnIndex)/(long)outputVideo.imageWidth);

							outputVideo.setPixelValue(inputVideo.accessPixelValue(inputRowIndex, inputColumnIndex, channel, frameNumber),rowIndex,columnIndex,channel,frameNumber);

						}
					}

				}	
			}


		}else{
			
			for(int frameNumber = 1; frameNumber <= outputVideo.totalFrames; frameNumber++){
				for(int channel = 0; channel < 3 ; channel++){
					for(int rowIndex = 0; rowIndex < outputVideo.imageHeight; rowIndex++){
						for(int columnIndex = 0; columnIndex < outputVideo.imageWidth; columnIndex++){
							
							int inputRowIndex = (int)(((long)(inputVideo.imageHeight)*(long)rowIndex)/(long)outputVideo.imageHeight);
							
							int inputColumnIndex = (int)(((long)(inputVideo.imageWidth)*(long)columnIndex)/(long)outputVideo.imageWidth);

							outputVideo.setPixelValue(inputVideo.accessPixelValue(inputRowIndex, inputColumnIndex, channel, frameNumber),rowIndex,columnIndex,channel,frameNumber);

						}
					}

				}	
			}
			
		
		}


		System.out.println("\033[0;32mConversion complete \033[0m");
		
		// Save video and return
		outputVideo.saveVideo("output"+outputFileName);
		
		return;


	}


//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Main
	public static void main(String[] args){
	
		convertFormat mainProg =  new convertFormat(args);
	}



}
