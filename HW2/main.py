# Importing libraries
import sys
import os
import numpy as np 
from tkinter import *
from PIL import Image, ImageTk
import math
import matplotlib
matplotlib.use('qt5agg')
import matplotlib.pyplot as plt

#-----------------------------------------------------------------------------------------------#
# Function for reading .RGB format file
def readImage(FILE_NAME, HEIGHT, WIDTH, CHANNELS):
	rgbImage = np.fromfile(FILE_NAME, dtype ='uint8')
	rgbArray = np.zeros((HEIGHT,WIDTH,CHANNELS),'float64')
	i,j,k = 0,HEIGHT*WIDTH*1,HEIGHT*WIDTH*2
	
	for channel in range(CHANNELS):
		for r in range(HEIGHT):
			for c in range(WIDTH):
				i = channel*HEIGHT*WIDTH+r*WIDTH+c
				rgbArray[r][c][channel] = rgbImage[i]
	
	if(CHANNELS==1):
		rgbArray = np.reshape(rgbArray,(HEIGHT,WIDTH))
		rgbArray_f = Image.fromarray(np.uint8(rgbArray),mode = 'L')
	else:
		rgbArray_f = Image.fromarray(np.uint8(rgbArray))
	
	#rgbArray_f.show()
	return rgbArray

#-----------------------------------------------------------------------------------------------#
# Perform k means:
def kmeans(codebook, pixelVectors,n):
	
	iter = 0
	diff = np.inf
	while(diff>0.001 and iter!=100):
		clusterPoints = np.full(len(pixelVectors),-1)
		newClusters = [[-1,-1] for i in range(0,n)]
		count = np.zeros(n)
			
		# Assign cluster to each point
		for index,vector in enumerate(pixelVectors):
			clusterPoint = -1
			minCostValue = np.inf
			for i in codebook:
				costValue = cost(codebook[i],vector)
				if(minCostValue > costValue):
					clusterPoint = i
					minCostValue = costValue
			assert (clusterPoint>=0),'Error finding centroid in kmeans'
			clusterPoints[index] = clusterPoint

		# Update codebooks:
		for index,vector in enumerate(pixelVectors):
			newClusters[clusterPoints[index]][0] += vector[0] 
			newClusters[clusterPoints[index]][1] += vector[1]
			count[clusterPoints[index]] +=1

		# Normalize newCluster Points:
		diff = 0
		for index,value in enumerate(newClusters):
			if count[index] != 0:
				newClusters[index][0] /= count[index]
				newClusters[index][1] /= count[index]
				diff += cost(newClusters[index],codebook[index])
				codebook[index] = newClusters[index]

		# Check for halting condition and update clusters:
		print('error in ',iter, ' = ',diff)	
		iter += 1

	print('quality of clustering = ', (len(np.unique(clusterPoints))/n))
	return clusterPoints
#-----------------------------------------------------------------------------------------------#
# Cost funtion
def cost(centroid, vectorPoint):
	euclideanDistance = math.sqrt((centroid[0]-vectorPoint[0])**2+(centroid[1]-vectorPoint[1])**2)
	assert euclideanDistance >=0 , 'Cost function is negative'
	return euclideanDistance

#-----------------------------------------------------------------------------------------------#
# Function for initialize k points:
def initializePoints(n):
	codebook= {}
	
	for i in range(0,n):
		codebook[i] = [np.random.randint(0,255),np.random.randint(0,255)]
	
	'''
	for r in range(1,int(n/2)+1):
		for c in range(1,int(n/2)+1):
			codebook[i] = [((r*255.0)/((n/2)+1)),(c*255.0)/((n/2)+1)]
			i = i+1	
	'''
	
	#Print codebook:
	'''		
	for items in codebook:
		print(codebook[items])
	'''
	return codebook


#-----------------------------------------------------------------------------------------------#
# Function for extracting vector points:
def extractVectors(rawArray):
	pixelVectors = []
	
	# loop through pixel Array:
	for r in range(0,len(rawArray)):
		for c in range(0,len(rawArray[0]),2):
			pixelVectors.append((rawArray[r][c],rawArray[r][c+1]))

	# Plot:
	'''	
	zip(*pixelVectors)
	plt.scatter(*zip(*pixelVectors))	
	plt.show()
	'''

	return pixelVectors
	

#-----------------------------------------------------------------------------------------------#
# Visulize codebook
def plotCodeBook(codebook,pixelVectors):
	zip(*pixelVectors)
	plt.scatter(*zip(*pixelVectors))	
	
	for keys in codebook:
		plt.scatter(codebook[keys][0], codebook[keys][1] ,color = 'red')
	
	plt.show()


#-----------------------------------------------------------------------------------------------#
# Display image:
def displayImage(clusterPoints, codebook,HEIGHT,WIDTH,pixelData):	
	
	rawArray = np.zeros((HEIGHT,WIDTH),'float64')

	i = 0
	for r in range(0,len(rawArray)):
		for c in range(0,len(rawArray[0]),2):
			rawArray[r][c] = codebook[clusterPoints[i]][0]
			rawArray[r][c+1] = codebook[clusterPoints[i]][1]
			i = i+1
	rawArray = np.reshape(rawArray,(HEIGHT,WIDTH))
	compressedImage = Image.fromarray(np.uint8(rawArray), mode = 'L')
	originalImage = Image.fromarray(np.uint8(pixelData),mode = 'L')
	
	# Open Tkinter:
	root = Tk()
	img1 = ImageTk.PhotoImage(originalImage)
	img2 = ImageTk.PhotoImage(compressedImage)
	panel1 = Label(root, image = img1)
	panel2 = Label(root, image = img2)
	var1 = StringVar()
	var1.set("Original Image")
	var2 = StringVar()
	var2.set("Decompressed Image")
	text1 = Label(root, textvariable = var1)
	text2 = Label(root, textvariable = var2)
	
	panel1.pack(side = "left", fill = "both", expand = "yes")
	panel2.pack(side = "right", fill = "both", expand = "yes")
	
	text1.pack(side = "bottom")
	text2.pack(side="bottom")
	
	panel1.grid(row =0 , column =0)
	panel2.grid(row =0 , column =1)
	text1.grid(row =1 , column =0)
	text2.grid(row =1 , column =1)
	
	root.mainloop()

#-----------------------------------------------------------------------------------------------#
# Main:
def main():
	
	# Read arguments:
	FILENAME = sys.argv[1];
	n = int(sys.argv[2]);
	assert (((n)&(n-1))==0 and n!=0),'Please input an integer in powers of 2 except 0!'
	extension = FILENAME.split('.')[1]

	# Set image parameters:
	HEIGHT = 288
	WIDTH = 352
	CHANNELS  =-1
	
	if extension == 'raw':
		CHANNELS = 1
	else:
		CHANNELS = 3

	assert (CHANNELS!=-1),'Incorrect file format!'

	# Print image type:
	print('Image: Width:',WIDTH,' Height:', HEIGHT,' CHANNELS:', CHANNELS)

	# Read image:
	pixelData = readImage(FILENAME,HEIGHT,WIDTH,CHANNELS)

	# Extract Vector points:

	pixelVectors = extractVectors(pixelData)
	
	# Initialize codebook
	codebook = initializePoints(n)
	
	# Optimize points:
	clusterPoints = kmeans(codebook, pixelVectors,n)
	
	# Visualize result:
	#plotCodeBook(codebook,pixelVectors)
	
	# Compressed Image display:
	displayImage(clusterPoints,codebook, HEIGHT,WIDTH,pixelData)


#-----------------------------------------------------------------------------------------------#
# Boilerplate code:

if __name__ == '__main__':
	main()

