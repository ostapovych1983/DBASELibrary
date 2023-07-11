package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums;

import java.nio.charset.Charset;

public enum DBFCodePage {
    NONE(0x00,0, Charset.forName("Windows-1251")),
   	US_MS_DOS( 0X01,437, Charset.forName("Cp437")),
    INT_MS_DOS(0X02,	850, Charset.forName("Cp850")),
    WINDOWS_ANSI_LATIN_I(0X03,	1252, Charset.forName("Cp1252")),
	STANDARD_MACINTOSH(0X04,10000, Charset.forName("MacCentralEurope")),
	DANISH_OEM(0X08,865, Charset.forName("Cp865")),
	DUTCH_OEM(0X09,437, Charset.forName("Cp437")),
	DUTCH_OEM_INT(0X0A,850, Charset.forName("Cp850")),
	FINNISH_OEM(0X0B,437, Charset.forName("Cp437")),
	FRENCH_OEM(0X0D,437, Charset.forName("Cp437")),
	FRENCH_OEM_INT(0X0E,850, Charset.forName("Cp850")),
	GERMAN_OEM(0X0F,437, Charset.forName("Cp437")),
	GERMAN_OEM_INT(0X10,850, Charset.forName("Cp850")),
    ITALIAN_OEM(0X11,437, Charset.forName("Cp437")),
	ITALIAN_OEM_INT(0X12,850, Charset.forName("Cp850")),
	SPANISH_OEM_INT(0X14,850, Charset.forName("Cp850")),
    SWEDISH_OEM(0X15,437, Charset.forName("Cp437")),
	SWEDISH_OEM_INT(0X16,850, Charset.forName("Cp850")),
    NORWEGIAN_OEM(0X17,865, Charset.forName("Cp865")),
	SPANISH_OEM(0X18,437, Charset.forName("Cp437")),
	ENGLISH_OEM_GREAT_BRITAIN(0X19,437, Charset.forName("Cp437")),
	ENGLISH_OEM_GREAT_BRITAIN_INT(0X1A,850, Charset.forName("Cp850")),
    ENGLISH_OEM_US(0X1B,437, Charset.forName("Cp437")),
	FRENCH_OEM_CANADA(0X1C,863, Charset.forName("Cp863")),
	FRENCH_OEM_INT850(0X1D,850, Charset.forName("Cp850")),
    CZECH_OEM(0X1F,852, Charset.forName("Cp852")),
	HUNGARIAN_OEM(0X22,852, Charset.forName("Cp852")),
	POLISH_OEM(0X23,852, Charset.forName("Cp852")),
	PORTUGUESE_OEM(0X24,860, Charset.forName("Cp860")),
	PORTUGUESE_OEM_INT(0X25,850, Charset.forName("Cp850")),
    RUSSIAN_OEM(0X26,866, Charset.forName("Cp866")),
	ENGLISH_OEM_US_INT(0X37,850, Charset.forName("Cp850")),
    ROMANIAN_OEM(0X40,852, Charset.forName("Cp852")),
	CHINESE_GBK_PRC(0X4D,936, Charset.forName("Cp936")),
	KOREAN_ANSI_OEM(0X4E,949, Charset.forName("Cp949")),
	CHINESE_BIG5_TAIWAN(0X4F,950, Charset.forName("Cp950")),
	THAI_ANSI_OEM(0X50,874, Charset.forName("Cp874")),
	ANSI(0X57,999, Charset.forName("Cp1252")),
	WESTERN_EUROPEAN_ANSI(0X58,1252, Charset.forName("Cp1252")),
	SPANISH_ANSI(0X59,1252, Charset.forName("Cp1252")),
	EASTERN_EUROPEAN_MS_DOS(0X64,852, Charset.forName("Cp852")),
	RUSSIAN_MS_DOS(0X65,866, Charset.forName("Cp866")),
	NORDIC_MS_DOS(0X66,865, Charset.forName("Cp865")),
	ICELANDIC_MS_DOS(0X67,861, Charset.forName("Cp861")),
	GREEK_MS_DOS_437G(0X6A,737, Charset.forName("Cp737")),
	TURKISH_MS_DOS(0X6B,857, Charset.forName("Cp857")),
	FRENCH_CANADIAN_MS_DOS(0X6C,863, Charset.forName("Cp863")),
	TAIWAN_BIG_5(0X78,950, Charset.forName("Cp950")),
    HANGUL_WANSUNG(0X79,949, Charset.forName("Cp949")),
	PRC_GBK(0X7A,936, Charset.forName("Cp936")),
	THAI_WINDOWS_MS_DOS(0X7C,874, Charset.forName("Cp874")),
	GREEK_OEM(0X86,737, Charset.forName("Cp737")),
	SLOVENIAN_OEM(0X87,852, Charset.forName("Cp852")),
	TURKISH_OEM(0X88,857, Charset.forName("Cp857")),
	RUSSIAN_MACINTOSH(0X96,10007, Charset.forName("MacCyrillic")),
	EASTERN_EUROPEAN_MACINTOSH(0X97,10029, Charset.forName("MacCyrillic")),
	GREEK_MACINTOSH(0X98,10006, Charset.forName("MacGreek")),
	EASTERN_EUROPEAN_WINDOWS(0XC8,1250, Charset.forName("Cp1250")),
	RUSSIAN_WINDOWS(0XC9,1251, Charset.forName("Cp1251")),
	TURKISH_WINDOWS(0XCA,1254, Charset.forName("Cp1254")),
	GREEK_WINDOWS(0XCB,1253, Charset.forName("Cp1253")),
	BALTIC_WINDOWS(0XCC,1257, Charset.forName("Cp1257"));
    private final short code;

	private final int internationCode;
    private final Charset javaCharSet;

    public static DBFCodePage readByCode(int code){
    	short codePage = (short) code;
    	for (DBFCodePage dbfCodePage:values()){
    		if (dbfCodePage.getCode() == codePage) return dbfCodePage;
		}
    	return NONE;
	}
	public static DBFCodePage readByInternationCode(int internationCode){
		for (DBFCodePage dbfCodePage:values()){
			if (dbfCodePage.internationCode == internationCode) return dbfCodePage;
		}
		return NONE;
	}

    DBFCodePage(int code, int intCode, Charset javaCharSet) {
        this.code = (short) code;
        this.internationCode = intCode;
		this.javaCharSet = javaCharSet;
	}

    public short getCode() {
        return code;
    }

	public Charset getJavaCharSet() {
		return javaCharSet;
	}

	@Override
	public String toString() {
		return "DBFCodePage{" +
				"internationCode=" + internationCode +
				'}';
	}
}
