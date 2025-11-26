package com.amay.tvm.overheadDisplay;

public class OverheadDisplayModes {
    public static class UserPartInfo {
        public int iX, iY, iWidth, iHeight, iFrameMode;
        public int frameColor;
    }

    public static class UserFontSet {
        public String strFontName;
        public int iFontSize;
        public boolean bFontBold, bFontItalic, bFontUnderline;
        public int colorFont;
        public int iAlignStyle, iVAlignerStyle;
        public int iRowSpace;
    }

    public static class UserText {
        public UserPartInfo partInfo;
        public UserFontSet fontInfo;
        public String strText;
    }

}
