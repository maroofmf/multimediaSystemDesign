import java.util.*;
import java.io.*;

public class videoData{

//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Private variables

public int imageWidth = 0;
public int imageHeight = 0;
public int totalFrames = 0;
public long fileSize = 0;
public byte[] pixelBuffer;
static double[][] weights = {{0.107035,0.11309,0.107035},{0.113092,0.119491,0.113092},{0.107035,0.113092,0.107035}}; 
//static double[][] weights = {{1,1,1},{1,1,1},{1,1,1}}; 
//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Default Constructor

public videoData(int imageWidth1, int imageHeight1){

	this.imageWidth = imageWidth1;
	this.imageHeight = imageHeight1;
}


//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Constructor II : For output buffered image

public videoData(int imageWidth1, int imageHeight1, long fileSize1 ){

	this.imageWidth = imageWidth1;
	this.imageHeight = imageHeight1;
	this.fileSize = fileSize1;
	pixelBuffer = new byte[(int)fileSize];
	totalFrames = (int)(fileSize/(long)(3*imageHeight*imageWidth));

}

//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Save data based on frame number, row, column:

public void setPixelValue(byte pixelValue,int rowIndex, int columnIndex, int channel, int frameNumber){

	// Check arguments
	if(channel == 3){
		System.out.println("Channel cannot be zero. R = 0, G =1, B = 2");
		System.exit(-1);
	}

	if(frameNumber > totalFrames ||rowIndex >= imageHeight|| columnIndex >= imageWidth ){
		System.out.println("Index out of bounds!");
		System.exit(-1);
	}

	pixelBuffer[((frameNumber-1)*3*imageHeight*imageWidth + channel*imageHeight*imageWidth + rowIndex*imageWidth+columnIndex)] = pixelValue;

}

//---------------------------------------------------------------------------------------------------------------------------------------------------//
// access data based on frame number, row, column with bilinear interpolation:

public byte accessPixelValue(double rowIndex, double columnIndex, int channel, int frameNumber){

	// Check arguments
	if(channel == 3){
		System.out.println("Channel cannot be zero. R = 0, G =1, B = 2");
		System.exit(-1);
	}

	if(frameNumber > totalFrames ||rowIndex >= imageHeight|| columnIndex >= imageWidth ){
		System.out.println("Index out of bounds!");
		System.exit(-1);
	}

	// Perform interpolation
	byte upLeftPixel = accessPixelValue((int)Math.floor(rowIndex),(int)Math.floor(columnIndex),channel,frameNumber);
	byte upRightPixel = accessPixelValue((int)Math.floor(rowIndex), (int)Math.ceil(columnIndex), channel, frameNumber);
	byte downLeftPixel = accessPixelValue((int)Math.ceil(rowIndex), (int)Math.floor(columnIndex), channel, frameNumber);
	byte downRightPixel = accessPixelValue((int)Math.ceil(rowIndex),(int)Math.floor(columnIndex), channel, frameNumber);
	
	double a = rowIndex - Math.floor(rowIndex);
	double b = columnIndex - Math.floor(columnIndex);

	byte pixelValue = (byte)((double)upLeftPixel*(1-a)*(1-b)+(double)upRightPixel*(1-a)*(b) + (double)downLeftPixel*(a)*(1-b) + (double)downRightPixel*(a)*(b));
	return pixelValue;
}
//---------------------------------------------------------------------------------------------------------------------------------------------------//
// access data based on frame number, row, column:

public byte accessPixelValue(int rowIndex, int columnIndex, int channel, int frameNumber){

	// Check arguments
	if(channel == 3){
		System.out.println("Channel cannot be zero. R = 0, G =1, B = 2");
		System.exit(-1);
	}

	if(frameNumber > totalFrames ||rowIndex >= imageHeight|| columnIndex >= imageWidth ){
		System.out.println("Index out of bounds!");
		System.exit(-1);
	}

	return pixelBuffer[((frameNumber-1)*3*imageHeight*imageWidth + channel*imageHeight*imageWidth + rowIndex*imageWidth+columnIndex)];

}

//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Gaussian filtering

public void gaussianFiltering(){

	System.out.println("\033[0;31mApplying Anti-aliasing filter \033[0m");
	
	// Create new buffer object
	videoData newBuffer = new videoData(imageWidth, imageHeight, fileSize);
	newBuffer.pixelBuffer = pixelBuffer;

	for(int frameNumber = 1; frameNumber<=totalFrames; frameNumber++){
		for(int channel = 0; channel < 3; channel++){
			for(int rowIndex = 1; rowIndex < imageHeight-1; rowIndex++){
				for(int columnIndex = 1; columnIndex < imageWidth-1;columnIndex++){
					
					// Take window
					double pixelValue = 0;
					for(int rowWindow = -1; rowWindow < 2; rowWindow++){
						for(int colWindow = -1; colWindow < 2; colWindow++){
							
							// Decoding pixel for mean values
							byte pixelEncoded = accessPixelValue(rowIndex+rowWindow,columnIndex+colWindow,channel,frameNumber);          
							double pixelDecoded = ((pixelEncoded)&(0xff));
							if(pixelDecoded < 0 ) System.exit(-1);
							pixelValue +=  weights[rowWindow+1][colWindow+1]*pixelDecoded;
						}

					}
					
					// Assertions
					if(pixelValue > 255) pixelValue = 255;
					assert pixelValue >0;

					// pixel encoding:
					byte finalPixelEncoding;
					if (pixelValue>127.5) finalPixelEncoding = (byte)(Math.round(pixelValue)-256); 
					else finalPixelEncoding = (byte)(Math.round(pixelValue));

					newBuffer.setPixelValue(finalPixelEncoding, rowIndex, columnIndex, channel, frameNumber);

				}
			}
		}
	}

	pixelBuffer = newBuffer.pixelBuffer;

	System.out.println("\033[0;32mAnti-aliasing complete\033[0m");

}

//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Load video data into 1d Array

public void readVideo(String videoPath){

		// Check if the image dimensions are known
		if(imageWidth == 0 || imageHeight == 0){
			System.out.println("\033[0;31mSpecify video dimensions before reading the file!\033[0m");
			System.exit(0);
		}

		// Read the video file
		try{
			File inputFile = new File(videoPath);
			InputStream stream = new FileInputStream(inputFile);
			fileSize = inputFile.length();
			pixelBuffer  = new byte[(int)fileSize];
			totalFrames = (int)(fileSize/(long)(3*imageHeight*imageWidth));
			
			int offset = 0;
			int numRead = 0;

			// Store file data into pixel buffer for each frame
			while(offset < pixelBuffer.length && (numRead = stream.read(pixelBuffer, offset, pixelBuffer.length-offset))>=0){
				offset += numRead;
			}

			stream.close();

		} catch(FileNotFoundException e){
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}

}


//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Save video data into a given path

public void saveVideo(String outputVideoPath){

	
	System.out.println("\033[0;31mSaving video....\033[0m");
	
	// Save pixels into the output path

	try{
		File outputFile = new File(outputVideoPath);
		OutputStream stream = new FileOutputStream(outputFile);
		
		// Store file data into pixel buffer for each frame
		stream.write(pixelBuffer);
		stream.close();

	} catch(FileNotFoundException e){
		e.printStackTrace();
	} catch(IOException e){
		e.printStackTrace();
	}
	
	System.out.println("\033[0;32mSaved!\033[0m");

}

}
