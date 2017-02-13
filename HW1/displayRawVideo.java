import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public class displayRawVideo{

//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Static Private Variables

private static GridBagConstraints c = new GridBagConstraints();
private static JLabel lbIm1 = new JLabel();
private int imageWidth = 256;
private int imageHeight = 256;
private int frameRate = 10;
private static int frameNumber = 1;
byte[] pixelBuffer = {};
private BufferedImage img;	
private JFrame frame = new JFrame();
private Timer timer;
private long fileSize;	
private double numberOfFrames;
private JLabel label;
//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Constructor

	public displayRawVideo(String[] args){
		
		System.out.println("\033[0;31mInitializing Display Program\033[0m");

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
		img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);		
		display_rgb(fileName);
		
	}

//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Updates next frame into image buffer

	 private void getNextFrame(){
			
			// Set pixel values based on location
			int ind = 0;
			int frameNumberCorrection = (frameNumber-1)*3*imageWidth*imageHeight;
			for(int rowIndex =0; rowIndex < imageHeight; rowIndex++){
				for(int columnIndex = 0; columnIndex < imageWidth; columnIndex++){
					
					byte redPixel = pixelBuffer[rowIndex*imageWidth+columnIndex+frameNumberCorrection];
					byte greenPixel = pixelBuffer[rowIndex*imageWidth+columnIndex+imageWidth*imageHeight+frameNumberCorrection];
					byte bluePixel = pixelBuffer[rowIndex*imageWidth+columnIndex+2*imageWidth*imageHeight+frameNumberCorrection];
					
					int pixelValue = 0xff000000 | ((redPixel&0xff)<<16) | ((greenPixel&0xff)<<8) | (bluePixel&0xff);
					img.setRGB(columnIndex,rowIndex,pixelValue);
					ind++;
				}
			}
			frameNumber++;

	}

//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Create initial GUI and display image

	private void createGUI(String topLabel){

		// Display image
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);
		label = new JLabel(topLabel);
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
// Update Video finish status
	private void updateFinish(){
		
		JLabel label1 = new JLabel("Video Finished");
		c.gridy = 0;
		frame.getContentPane().remove(label);
		frame.getContentPane().add(label1,c);
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		
		return;
	}

//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Update Next Frame

	private void updateFrame(){
		
		lbIm1.setIcon(new ImageIcon(img));
		frame.getContentPane().add(lbIm1,c);
		frame.pack();

	}	

//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Display image/video
	public void display_rgb(String fileName){
		
		try{
			File inputFile = new File(fileName);
			InputStream stream = new FileInputStream(inputFile);
			fileSize = inputFile.length();
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
		
		// Calculate video params
		numberOfFrames = ((double)fileSize)/(double)(3.0*imageWidth*imageHeight);
		long timeInterval = (long)1000.00/(long)frameRate;

		// Initializing synchronization
		synchronize sync = new synchronize();
		timer = new Timer((int)timeInterval, sync);
		timer.start();
		
	}

//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Nested class for frame synchronization

	class synchronize implements ActionListener{
		
		public void actionPerformed(ActionEvent e){

			if(numberOfFrames > 1){
				getNextFrame();
				updateFrame();
				numberOfFrames--;
			}else{
				updateFinish();
				timer.stop();
			}
		}

		public synchronize(){
			super();
			// Display initial frame
			getNextFrame();
			createGUI(String.format("w = %d, h= %d, fps = %d",imageWidth,imageHeight,frameRate));			
		}
	
	}

//---------------------------------------------------------------------------------------------------------------------------------------------------//
// Main

	public static void main(String[] args){		
		displayRawVideo reader = new displayRawVideo(args);
	}

}
