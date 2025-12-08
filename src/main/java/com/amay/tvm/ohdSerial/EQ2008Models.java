package com.amay.tvm.ohdSerial;

import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;

public class EQ2008Models {

    public static class User_FontSet extends Structure {
        public String fontName;
        public int fontSize;
        public int color;
        public int bold;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("fontName","fontSize","color","bold");
        }
    }
}
