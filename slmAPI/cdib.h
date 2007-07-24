////////////////////////////////////////////////////////////////////////////////////
// cdib.h declaration for Inside Visual C++ CDib class
//
// The CDib class is used by Blink to read in Bitmaps
/////////////////////////////////////////////////////////////////////////////////////

#ifndef _INSIDE_VISUAL_CPP_CDIB
#define _INSIDE_VISUAL_CPP_CDIB

class CDib : public CObject
{
	enum Alloc {noAlloc, crtAlloc, heapAlloc};
	DECLARE_SERIAL(CDib)
public:
	LPVOID m_lpvColorTable;
	HBITMAP m_hBitmap;
	LPBYTE m_lpImage;  // starting address of DIB bits
	LPBITMAPINFOHEADER m_lpBMIH; //  buffer containing the BITMAPINFOHEADER

private:
	HGLOBAL m_hGlobal; // For external windows we need to free;
	                   //  could be allocated by this class or allocated externally
	Alloc m_nBmihAlloc;
	Alloc m_nImageAlloc;
	DWORD m_dwSizeImage; // of bits -- not BITMAPINFOHEADER or BITMAPFILEHEADER
	unsigned short m_nColorTableEntries;
	
	HANDLE m_hFile;
	HANDLE m_hMap;
	LPVOID m_lpvFile;
	HPALETTE m_hPalette;
public:
	CDib();
	~CDib();
	int GetSizeImage() {return m_dwSizeImage;}
	int GetSizeHeader()
		{return sizeof(BITMAPINFOHEADER) + sizeof(RGBQUAD) * m_nColorTableEntries;}
	CSize GetDimensions();

	BOOL MakePalette();
	BOOL Read(CFile* pFile);
	void Empty();

private:
	void DetachMapFile();
	void ComputePaletteSize(int nBitCount);
	void ComputeMetrics();
};
#endif // _INSIDE_VISUAL_CPP_CDIB
