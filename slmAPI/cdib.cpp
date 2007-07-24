// cdib.cpp
// new version for WIN32
#include "stdafx.h"
#include "cdib.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

IMPLEMENT_SERIAL(CDib, CObject, 0);

//This is the constructor of the CDib class. This must be called before any
//of the CDib functions can be accessed.
CDib::CDib()
{
	m_hFile = NULL;
	m_hBitmap = NULL;
	m_hPalette = NULL;
	m_nBmihAlloc = m_nImageAlloc = noAlloc;
	Empty();
}

//This is the destructor of the CDib class. This should be called when finished
//with the CDib functions to clean up any allocations.
CDib::~CDib()
{
	Empty();
}

//This function will return the dimensions of a bitmap
CSize CDib::GetDimensions()
{	
	if(m_lpBMIH == NULL) return CSize(0, 0);
	return CSize((int) m_lpBMIH->biWidth, (int) m_lpBMIH->biHeight);
}

//This function generates the palatte for a bitmap
BOOL CDib::MakePalette()
{
	// makes a logical palette (m_hPalette) from the DIB's color table
	// this palette will be selected and realized prior to drawing the DIB	if(m_nColorTableEntries == 0) return FALSE;
	if(m_hPalette != NULL) ::DeleteObject(m_hPalette);
//	TRACE("CDib::MakePalette -- m_nColorTableEntries = %d\n", m_nColorTableEntries);
	LPLOGPALETTE pLogPal = (LPLOGPALETTE) new char[2 * sizeof(WORD) +
		m_nColorTableEntries * sizeof(PALETTEENTRY)];
	if (pLogPal == NULL)
	{
		AfxMessageBox("Error allocating memory for MakePalette", MB_OK);
	}

	pLogPal->palVersion = 0x300;
	pLogPal->palNumEntries = m_nColorTableEntries;

//	LPRGBQUAD pDibQuad = (LPRGBQUAD) m_lpvColorTable;
	for(int i = 0; i < m_nColorTableEntries; i++)
	{
		pLogPal->palPalEntry[i].peRed = i;
		pLogPal->palPalEntry[i].peGreen = i;
		pLogPal->palPalEntry[i].peBlue = i;
		pLogPal->palPalEntry[i].peFlags = PC_RESERVED;
	}
	m_hPalette = ::CreatePalette(pLogPal);
	//BNS insert to the free code
	CDC *pWindDC;
	pWindDC = AfxGetMainWnd()->GetDC();
	HDC hDC = pWindDC->GetSafeHdc();

//	SetSystemPaletteUse(hDC, SYSPAL_NOSTATIC);
	::SelectPalette(hDC, m_hPalette, false);
	::RealizePalette(hDC);
	//end BNS insert
	AfxGetMainWnd()->ReleaseDC(pWindDC);
	delete pLogPal;
	return TRUE;
}	

//This function will actually read in the pixel values of the bitmap. Pass in the
//path to the file you want to read, then all the pixel values will be accessible.
BOOL CDib::Read(CFile* pFile)
{
	// 1. read file header to get size of info hdr + color table
	// 2. read info hdr (to get image size) and color table
	// 3. read image
	// can't use bfSize in file header
	Empty();
	int nCount, nSize;
	BITMAPFILEHEADER bmfh;
	try {
		nCount = pFile->Read((LPVOID) &bmfh, sizeof(BITMAPFILEHEADER));
		if(nCount != sizeof(BITMAPFILEHEADER)) {
			throw new CException;
		}
		if(bmfh.bfType != 0x4d42) {
			throw new CException;
		}
		nSize = bmfh.bfOffBits - sizeof(BITMAPFILEHEADER);
		m_lpBMIH = (LPBITMAPINFOHEADER) new char[nSize];
		if (m_lpBMIH == NULL)
		{
			AfxMessageBox("Error allocating memory for CDib,Read", MB_OK);
		}

		m_nBmihAlloc = m_nImageAlloc = crtAlloc;
		nCount = pFile->Read(m_lpBMIH, nSize); // info hdr & color table
		ComputeMetrics();
		ComputePaletteSize(m_lpBMIH->biBitCount);
		MakePalette();
		m_lpImage = (LPBYTE) new char[m_dwSizeImage];
		if (m_lpImage == NULL)
		{
			AfxMessageBox("Error allocating memory for image CDib:Read", MB_OK);
		}

		nCount = pFile->Read(m_lpImage, m_dwSizeImage); // image only
	}
	catch(CException* pe) {
		AfxMessageBox("Read error");
		pe->Delete();
		return FALSE;
	}
	return TRUE;
}


// HELPER FUNCTIONS
void CDib::ComputePaletteSize(int nBitCount)
{
	if((m_lpBMIH == NULL) || (m_lpBMIH->biClrUsed == 0)) {
		switch(nBitCount) {
			case 1:
				m_nColorTableEntries = 2;
				break;
			case 4:
				m_nColorTableEntries = 16;
				break;
			case 8:
				m_nColorTableEntries = 256;
				break;
			case 16:
			case 24:
			case 32:
				m_nColorTableEntries = 0;
				break;
			default:
				ASSERT(FALSE);
		}
	}
	else {
		m_nColorTableEntries = (unsigned short)m_lpBMIH->biClrUsed;
	}
	ASSERT((m_nColorTableEntries >= 0) && (m_nColorTableEntries <= 256)); 
}

void CDib::ComputeMetrics()
{
	if(m_lpBMIH->biSize != sizeof(BITMAPINFOHEADER)) {
		TRACE("Not a valid Windows bitmap -- probably an OS/2 bitmap\n");
		throw new CException;
	}
	m_dwSizeImage = m_lpBMIH->biSizeImage;
	if(m_dwSizeImage == 0) {
		DWORD dwBytes = ((DWORD) m_lpBMIH->biWidth * m_lpBMIH->biBitCount) / 32;
		if(((DWORD) m_lpBMIH->biWidth * m_lpBMIH->biBitCount) % 32) {
			dwBytes++;
		}
		dwBytes *= 4;
		m_dwSizeImage = dwBytes * m_lpBMIH->biHeight; // no compression
	}
	m_lpvColorTable = (LPBYTE) m_lpBMIH + sizeof(BITMAPINFOHEADER);
}

void CDib::Empty()
{
	// this is supposed to clean up whatever is in the DIB
	DetachMapFile();
	if(m_nBmihAlloc == crtAlloc) {
		delete [] m_lpBMIH;
	}
	else if(m_nBmihAlloc == heapAlloc) {
		::GlobalUnlock(m_hGlobal);
		::GlobalFree(m_hGlobal);
	}
	if(m_nImageAlloc == crtAlloc) delete [] m_lpImage;
	if(m_hPalette != NULL) ::DeleteObject(m_hPalette);
	if(m_hBitmap != NULL) ::DeleteObject(m_hBitmap);
	m_nBmihAlloc = m_nImageAlloc = noAlloc;
	m_hGlobal = NULL;
	m_lpBMIH = NULL;
	m_lpImage = NULL;
	m_lpvColorTable = NULL;
	m_nColorTableEntries = 0;
	m_dwSizeImage = 0;
	m_lpvFile = NULL;
	m_hMap = NULL;
	m_hFile = NULL;
	m_hBitmap = NULL;
	m_hPalette = NULL;
}

void CDib::DetachMapFile()
{
	if(m_hFile == NULL) return;
	::UnmapViewOfFile(m_lpvFile);
	::CloseHandle(m_hMap);
	::CloseHandle(m_hFile);
	m_hFile = NULL;
}
