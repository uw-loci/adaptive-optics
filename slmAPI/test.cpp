#include "slmproject.h"
#include "test.h"

////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////
///////////////////////debug function///////////////////////////////////////

#ifdef DEBUG_SIGN
void debug_outdata(int size, CString filename, unsigned char *data)
{
    int tp, tp2;
    FILE *pfold;
	pfold = fopen(filename,"wt");
    for (tp = 0; tp < size; tp ++)
		for (tp2 = 0; tp2 < size; tp2++)
		fprintf(pfold, "%d\n", data[tp2*SLMSIZE + tp]);
	fclose(pfold);
}


void debug_outdata_double(int size, CString filename, double *data)
{
    int tp, tp2;
    FILE *pfold;
	pfold = fopen(filename,"wt");
    for (tp = 0; tp < size; tp ++)
		for (tp2 = 0; tp2 < size; tp2++)
		fprintf(pfold, "%f\n", data[tp2*SLMSIZE + tp]);
	fclose(pfold);
}


void debug_outdata_complex(int size, CString filename, COMPLEX **compdata)
{
    int tp, tp2;
    FILE *pfold;
	pfold = fopen(filename,"wt");
    for (tp = 0; tp < size; tp ++)
		for (tp2 = 0; tp2 < size; tp2++)
		fprintf(pfold, "r %f + i %f\n", compdata[tp][tp2].real, compdata[tp][tp2].imag);
	fclose(pfold);
}

#endif
////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////







void makedata(unsigned char *data, int rowlen, int collen)
{
	int row, col;

	for(row = 0; row < rowlen; row++)
	{
		for(col = 0; col < collen; col++)
		{
			if(((col+1) * (col+1)) + ((row+1) * (row+1)) < 100)
        	    data[(row)*collen + col] = 200;
			else
        	    data[(row)*collen + col] = 0;
		}
	}
    return;
}

void makedataline(unsigned char *data, int rowlen, int collen)
{
	int row, col;

	for(row = 0; row < rowlen; row++)
	{
		for(col = 0; col < collen; col++)
		{
			if((col+1)%4 == 0) 
        	    data[(row)*collen + col] = 200;
			else
        	    data[(row)*collen + col] = 0;
		}
	}
    return;
}


void makerandom(unsigned char *data, int rowlen, int collen)
{
	int row, col;

	for(row = 0; row < rowlen; row++)
	{
		for(col = 0; col < collen; col++)
		{
       	    data[(row)*collen + col] = rand()*255;
		}
	}
    return;
}





bool ReadZernikeFile(unsigned char *GeneratedImage, CString fileName)
{
	char inBuf[ZRN_MAX_LINE];
	char inputString[ZRN_MAX_LINE];
	char inputKey[ZRN_MAX_LINE];

	int row, col, Radius;
	double x, y, divX, divY, total, XSquPlusYSqu, XPYSquSqu, divXSqu, divYSqu;
	double term1, term2, term3, term4, term5, term6;
	double term7, term8, term9, term10, term11, term12;
	double term13, term14, term15, term16, term17, term18;
	double term25, term36;
	double Piston, XTilt, YTilt, Power, AstigOne, AstigTwo;
	double ComaX, ComaY, PrimarySpherical, TrefoilX, TrefoilY, SecondaryAstigX;
	double SecondaryAstigY, SecondaryComaX, SecondaryComaY, SecondarySpherical;
	double TetrafoilX, TetrafoilY, TertiarySpherical, QuaternarySpherical;

	//set our image size based on our board spec's if we are downloading the image
	//the radius can be varied, this is the radius of the cone that is generated
	//with the zernike polynomial equations, through tested we determined 
	//300 to be a good number. This means that the edge of the cone does extend
	//beyond the edge of the SLM, but not quite all the way to the corner of the SLM.
	//It is a happy medium between getting the cone to reach the coners without
	//chopping too much of the sides. Basically this is a problem of how you 
	//force a circle to fit in a square.
	Radius = ZERNIKERAD;

	//open the zernike file to read
	ifstream ZernikeFile(fileName);
	if (ZernikeFile.is_open())
	{
		while (ZernikeFile.getline(inBuf, ZRN_MAX_LINE, '\n'))
		{
			//read in a line from the file
			sscanf(inBuf,"%[^= ]%*[= ]%s", inputKey, inputString);
	
			//get the zernikes
			if(strcmp(inputKey,"Piston") == 0)
				Piston = atof(inputString);
			else if(strcmp(inputKey, "XTilt") == 0)
				XTilt = atof(inputString);
			else if(strcmp(inputKey, "YTilt") == 0)
				YTilt = atof(inputString);
			else if(strcmp(inputKey, "Power") == 0)
				Power = atof(inputString);
			else if(strcmp(inputKey, "AstigX") == 0)
				AstigOne = atof(inputString);
			else if(strcmp(inputKey,"AstigY") == 0)
				AstigTwo = atof(inputString);
			else if(strcmp(inputKey, "ComaX") == 0)
				ComaX = atof(inputString);
			else if(strcmp(inputKey, "ComaY") == 0)
				ComaY = atof(inputString);
			else if(strcmp(inputKey, "PrimarySpherical") == 0)
				PrimarySpherical = atof(inputString);
			else if(strcmp(inputKey, "TrefoilX") == 0)
				TrefoilX = atof(inputString);
			else if(strcmp(inputKey, "TrefoilY") == 0)
				TrefoilY = atof(inputString);
			else if(strcmp(inputKey,"SecondaryAstigX") == 0)
				SecondaryAstigX = atof(inputString);
			else if(strcmp(inputKey, "SecondaryAstigY") == 0)
				SecondaryAstigY = atof(inputString);
			else if(strcmp(inputKey, "SecondaryComaX") == 0)
				SecondaryComaX = atof(inputString);
			else if(strcmp(inputKey, "SecondaryComaY") == 0)
				SecondaryComaY = atof(inputString);
			else if(strcmp(inputKey, "SecondarySpherical") == 0)
				SecondarySpherical = atof(inputString);
			else if(strcmp(inputKey, "TetrafoilX") == 0)
				TetrafoilX = atof(inputString);
			else if(strcmp(inputKey, "TetrafoilY") == 0)
				TetrafoilY = atof(inputString);
			else if(strcmp(inputKey, "TertiarySpherical") == 0)
				TertiarySpherical = atof(inputString);
			else if(strcmp(inputKey, "QuaternarySpherical") == 0)
				QuaternarySpherical = atof(inputString);

		}//end while getline

		ZernikeFile.close();

		//now generate our image based on our zernike polynomials
		y = SLMSIZE/2;
		for(row = 0; row < SLMSIZE; row++)
		{
			//reset x
			x = (SLMSIZE/2)*-1;
			for(col = 0; col < SLMSIZE; col++)
			{
				//build some terms that are repeated through the equations
				divX = x/Radius;
				divY = y/Radius;
				XSquPlusYSqu = divX*divX + divY*divY;
				XPYSquSqu = XSquPlusYSqu*XSquPlusYSqu;
				divXSqu = divX*divX;
				divYSqu = divY*divY;

				//figure out what each term in the equation is
				term1 = (Piston/2);
				term2 = (XTilt/2)*divX;
				term3 = (YTilt/2)*divY;
				term4 = (Power/2)*(2*XSquPlusYSqu - 1);
				term5 = (AstigOne/2)*(divXSqu - divY*divY);
				term6 = (AstigTwo/2)*(2*divX*divY);

				term7 = (ComaX/2)*(3*divX*XSquPlusYSqu - 2*divX);
				term8 = (ComaY/2)*(3*divY*XSquPlusYSqu - 2*divY);
				term9 = (PrimarySpherical/2)*(1 - 6*XSquPlusYSqu + 6*XPYSquSqu);
				term10 = (TrefoilX/2)*(divXSqu*divX - 3*divX*divYSqu);
				term11 = (TrefoilY/2)*(3*divXSqu*divY - divYSqu*divY);
				term12 = (SecondaryAstigX/2)*(3*divYSqu - 3*divXSqu + 4*divXSqu*XSquPlusYSqu - 4*divYSqu*XSquPlusYSqu);

				term13 = (SecondaryAstigY/2)*(8*divX*divY*XSquPlusYSqu - 6*divX*divY);
				term14 = (SecondaryComaX/2)*(3*divX - 12*divX*XSquPlusYSqu + 10*divX*XPYSquSqu);
				term15 = (SecondaryComaY/2)*(3*divY - 12*divY*XSquPlusYSqu + 10*divY*XPYSquSqu);
				term16 = (SecondarySpherical/2)*(12*XSquPlusYSqu - 1 - 30*XPYSquSqu + 20*XSquPlusYSqu*XPYSquSqu);
				term17 = (TetrafoilX/2)*(divXSqu*divXSqu - 6*divXSqu*divYSqu + divYSqu*divYSqu);
				term18 = (TetrafoilY/2)*(4*divXSqu*divX*divY - 4*divX*divY*divYSqu);

				term25 = (TertiarySpherical/2)*(1 - 20*XSquPlusYSqu + 90*XPYSquSqu - 140*XSquPlusYSqu*XPYSquSqu + 70*XPYSquSqu*XPYSquSqu);		
				term36 = (QuaternarySpherical/2)*(-1 + 30*XSquPlusYSqu - 210*XPYSquSqu + 560*XPYSquSqu*XSquPlusYSqu - 630*XPYSquSqu*XPYSquSqu + 252*XPYSquSqu*XPYSquSqu*XSquPlusYSqu);

				//add those terms together
				total = term1+term2+term3+term4+term5+term6+
						term7+term8+term9+term10+term11+term12+
						term13+term14+term15+term16+term17+term18+
						term25+term36;
				//now scale it and assign the result to the array
				if (total >0)
					total = total - int(total);
				else
					total = total + int(total) + 1;
				GeneratedImage[row*SLMSIZE+col] = (int)((total)*255);

				x++;
			}//close col loop
			y--;
		}//close row loop

		return true;
	}
	//if we could not open zernike file
	else
	{
		memset(GeneratedImage, '0', SLMSIZE*SLMSIZE);
		return false;
	}
}




//////////////////////////////////////////////////////////////////////////////////
//
//  ReadAndInvertBitmap()
//
//  Inputs: empty array to fill, the file name to read in
//
//  Returns: true if no errors, otherwise false
//
//  Purpose: This function will read in the bitmap and x axis flip it. If there is a 
//			 problem reading in the bitmap, then we fill the array with zeros. This 
//			 function then calls ScaleBitmap so that we can scale the 
//			 bitmap to an images size based on the board type.
//			 
//  Modifications:
//
/////////////////////////////////////////////////////////////////////////////////////
bool ReadAndInvertBitmap(unsigned char *InvertedImage, CString fileName)
{
	int width, height, bytespixel;

	//initialize a tmpImage. Do not assign dimensions because it is not
	//safe to assume we know the dimensions prior to actually reading the bitmap.
	unsigned char* tmpImage;

	//get a handle to our file
	CFile *pFile = new CFile();
	if (pFile == NULL)
		printf("Error allocating memory for pFile,ReadandInvertBitmap\n");

	//if it is a bmp file and we can open it
	if (((fileName.Find(".BMP"))||(fileName.Find(".bmp")))&&
		(pFile->Open(fileName, CFile::modeRead | CFile::shareDenyNone, NULL)))
	{
		//read in the bitmap dimensions
		CDib dib;
		dib.Read(pFile);
		width = dib.GetDimensions().cx;
		height = dib.GetDimensions().cy;
		bytespixel = dib.m_lpBMIH->biBitCount;
		pFile->Close();
		delete pFile;
		
		//allocate our tmp array based on the bitmap dimensions
		tmpImage = new unsigned char[height*width];

		//perform the x axis flip on the image
		for (int i=0; i<height; i++)
		{
			for (int j=0; j<width; j++)
			{
				if (bytespixel == 4)
					tmpImage[((height-1)-i)*height+j] = dib.m_lpImage[i*(height/2)+(j/2)];
				if (bytespixel == 8)
					tmpImage[((height-1)-i)*height+j] = dib.m_lpImage[i*height+j];
				if (bytespixel == 16)
					tmpImage[((height-1)-i)*height+j] = dib.m_lpImage[i*2*height+j*2];
				if (bytespixel == 24)
					tmpImage[((height-1)-i)*height+j] = dib.m_lpImage[i*3*height+j*3];
			}
		}
 
		dib.~CDib();
	}
	//we could not open the file or the file was not a .bmp file
	else 
	{
		memset(InvertedImage, '0', SLMSIZE*SLMSIZE);
		return false;
	}

	//scale the image to the size of the SLM. This information is read from
	//the hardware. We assume that even if the bitmap is the wrong size, it is
	//square and divisable by 8
	//unsigned char* ScaledImage = ScaleBitmap(tmpImage, height, ImgHeight);

	//copy the scaled bitmap into the array passed into the function
	memcpy(InvertedImage, tmpImage, SLMSIZE*SLMSIZE);

	//delete our tmp array to avoid mem leaks
	delete []tmpImage;

	return true;
}


/*
/////////////////////////////////CLASS//////////////////////////////////////////
*/

slmAPI::slmAPI()
{
	
	//imagedata = (double *) malloc(SLMSIZE*SLMSIZE*sizeof(double));   
	fftcl = new fft2dTrans;
	slm = new SlmCom;
	slm->InitSlm();
}


void slmAPI::DtoI(double * Dbuf, int length)
{
	unsigned char temp;
	for (int i = 0; i< length; i ++)
	{
		temp = unsigned char(*(Dbuf+i));
		if ((*(Dbuf+i) - temp) < 0.5)
            *(phasedata+i) = temp;
		else
            *(phasedata+i) = temp + 1;
	}
}

void slmAPI::sendData(double *Data)
{
	bool framechange = 0;
/*
    imagedata = Data;
*/

#ifdef DEBUG_OUTPUT
    int tp, tp2;
    FILE *pfold;
	pfold = fopen("c:/slmcontrol/dumpout/beforefft.txt","wt");
    for (tp = 0; tp < 512; tp ++)
	{
		fprintf(pfold, "\n");
		for (tp2 = 0; tp2 < 512; tp2++)
		fprintf(pfold, "%f, ", Data[tp*512 + tp2]);
	}
	fclose(pfold);
#endif

/*
	fftcl->receivedata(imagedata, SLMSIZE, SLMSIZE);

	fftcl->fft2(SLMSIZE, SLMSIZE, 1);

	memset(imagedata,0,SLMSIZE*SLMSIZE*sizeof(double)); 
	memset(phasedata, 0, SLMSIZE*SLMSIZE*sizeof(unsigned char));

	fftcl->getPhase(imagedata);

    for (int i = 0; i< SLMSIZE*SLMSIZE; i ++)
	{
		if (*(imagedata+i) < 0)
		{
			*(phasedata+i) = unsigned char ((*(imagedata+i) + 2*pi)*255/(2*pi));
		}
		else
		{
		    *(phasedata+i) = unsigned char ((*(imagedata+i))*255/(2*pi));
		}
	}

	slm->receiveData(phasedata);
*/

  phasedata = (unsigned char *) malloc(SLMSIZE*SLMSIZE*sizeof(char));
	DtoI(Data, SLMSIZE*SLMSIZE);
	slm->receiveData(phasedata);
  free(phasedata);

	slm->SendtoDlm(framechange);
	return;
}



void slmAPI::powerOn()
{
    slm->InitSlm();
    return;
}


void slmAPI::powerOff()
{
    slm->CloseSlm();
	return;
}


#if 0
void main(void)
{
	bool framechange;
	double *imagedata;
	unsigned char *oldata;
	int ImgWidth, ImgHeight;
    SlmCom * slm = new SlmCom;

	slm->GetWH(&ImgWidth, &ImgHeight);

	oldata = (unsigned char *) malloc(ImgWidth*ImgHeight*sizeof(char));


//	makedata(oldata, ImgHeight, ImgWidth);
//	makedataline(oldata, ImgHeight, ImgWidth);
//  makerandom(oldata, ImgHeight, ImgWidth);

/*
	if (false == ReadZernikeFile(oldata, "zernikeImage.zrn"))
	{
	    printf("\nzernike polynomail create data error.\n");
		return;
	}
*/
    if(false == ReadAndInvertBitmap(oldata, "testread.bmp"))
	{
	    printf("\nthere is error when read image\n");
	}



#ifdef DEBUG_SIGN
////////////////////////////////////////////////////////////////////////
	debug_outdata(SLMSIZE, "oldata.txt", oldata);
////////////////////////////////////////////////////////////////////////

  //test to see the data by writing in a bmp file 
  char * file_out_name = "testbmp.bmp";

  error = bmp_08_write ( file_out_name, ImgWidth, ImgHeight, oldata );

  if ( error )
  {
    cout << "\n";
    cout << "BMP_08_WRITE - Fatal error!\n";
    cout << "  The test failed.\n";
    return;
  }
/////////////////////////////////////////////////////////////////////////////
#endif


	imagedata = (double *) malloc(ImgWidth*ImgHeight*sizeof(double));

    for (int i = 0; i< ImgHeight*ImgWidth; i ++)
	{
		*(imagedata+i) = double (*(oldata+i));
	}


	fft2dTrans * fftcl = new fft2dTrans;
	fftcl->receivedata(imagedata, ImgWidth, ImgHeight);


	fftcl->fft2(ImgWidth, ImgHeight, 1);

	memset(imagedata,0,ImgWidth*ImgHeight*sizeof(double)); 
	memset(oldata, 0, ImgWidth*ImgHeight*sizeof(unsigned char));

	fftcl->getPhase(imagedata);


    for (i = 0; i< ImgHeight*ImgWidth; i ++)
	{
		if (*(imagedata+i) < 0)
		{
			*(oldata+i) = unsigned char ((*(imagedata+i) + 2*pi)*255/(2*pi));
		}
		else
		{
		    *(oldata+i) = unsigned char ((*(imagedata+i))*255/(2*pi));
		}
	}


	slm->InitSlm();

	slm->receiveData(oldata);

	framechange = false;
	slm->SendtoDlm(framechange);
	free(imagedata);
	free(oldata);

	delete slm;
    delete fftcl;
	return;
}

#endif