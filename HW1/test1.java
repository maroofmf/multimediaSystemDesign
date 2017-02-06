import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;

public class test1{


//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Static Private Variables

private static GridBagConstraints c = new GridBagConstraints();
private static JLabel lbIm1 = new JLabel();
private int imageWidth = 256;
private int imageHeight = 256;
private int frameRate = 10;
private static int frameNumber = 1;
byte[] pixelBuffer = {};
private BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);		
private JFrame frame = new JFrame();

//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Updates  next fram into image buffer

	 private void getNextFrame(){
			
			// Set pixel values based on location
			int ind = 0;
			int frameNumberCorrection = (frameNumber-1)*3*imageWidth*imageHeight;
			for(int rowIndex =0; rowIndex < imageHeight; rowIndex++){
				for(int columnIndex = 0; columnIndex < imageWidth; columnIndex++){
					
					byte redPixel = pixelBuffer[ind+frameNumberCorrection];
					byte greenPixel = pixelBuffer[ind+imageWidth*imageHeight+frameNumberCorrection];
					byte bluePixel = pixelBuffer[ind+2*imageWidth*imageHeight+frameNumberCorrection];
					
					int pixelValue = 0xff000000 | ((redPixel&0xff)<<16) | ((greenPixel&0xff)<<8) | (bluePixel&0xff);
					img.setRGB(columnIndex,rowIndex,pixelValue);
					ind++;
				}
			}
			frameNumber++;
	}

//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Create initial GUI and display image
	private void createGUI(JFrame frame, String topLabel, BufferedImage img){

		// Display image
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);
		JLabel label = new JLabel(topLabel);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		
		// Image
		lbIm1.setIcon(new ImageIcon(img));

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		frame.getContentPane().add(label,c);
		
		c.gridy = 1;
		frame.getContentPane().add(lbIm1,c);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		
		System.out.println("\033[0;32mDisplay Window Up & Running!\033[0m");

	}


//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Create initial GUI and display image

	private void updateFrame(){
		
		lbIm1.setIcon(new ImageIcon(img));
		frame.getContentPane().add(lbIm1,c);
		frame.pack();
		//System.out.println("\033[0;32mUpdated Frame\033[0m");

	}	

//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Display image/video
	public void display_rgb(String fileName){
		
		try{
			File inputFile = new File(fileName);
			InputStream stream = new FileInputStream(inputFile);
			long fileSize = inputFile.length();
			pixelBuffer  = new byte[(int)fileSize];
			
			int offset = 0;
			int numRead = 0;

			// Store file data into pixel buffer for each frame
			while(offset < pixelBuffer.length && (numRead = stream.read(pixelBuffer, offset, pixelBuffer.length-offset))>=0){
				offset += numRead;
			}

		} catch(FileNotFoundException e){
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}
	
		// Display initial frame
		getNextFrame();
		createGUI(frame, "Test", img);			
		
		// Synchronize frames
		


		for(int i = 0; i< 99;i++){
			getNextFrame();
			updateFrame();
		//	try{

	
		//		Thread.sleep(90);
		//	}catch(InterruptedException e){
		//		Thread.currentThread().interrupt();
		//	}

		
		}


	}


//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Main

	//public static void main(String[] args){
		
	///	test1 a = new test1(args);
	//}

//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Constructor

	public test1(String[] args){
		
		System.out.println("\033[0;31mInitializing Program\033[0m");

		// Input Variables
		int argsLength = args.length;
		String fileName = "";
		
		// Parse Command Line Arguments
		if(argsLength < 3 || argsLength > 4){
			System.out.println("\033[0;31mWrong Usage! Correct Usage: java [program name] [.rgb file] [Image Width] [Image Height] [fps <optional>]\033[0m");
			System.exit(-1);
		}else if (argsLength < 4 ){
			fileName = args[0];
			imageWidth = Integer.parseInt(args[1]);
			imageHeight = Integer.parseInt(args[2]);
		} else if(argsLength == 4){	
			fileName = args[0];
			imageWidth = Integer.parseInt(args[1]);
			imageHeight = Integer.parseInt(args[2]);
			frameRate = Integer.parseInt(args[3]);
		}
		
		System.out.println("\033[0;32mSucessfully Initialized\033[;0m");

		// Display Image
		//test1 displayObject = new test1();
		display_rgb(fileName);
		

	}
}
