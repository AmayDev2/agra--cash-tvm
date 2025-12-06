package com.amay.tvm.eq2008;

import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;

public class EQ2008Models {

    // =============================
    //  DISPLAY / Bitmap Model
    // =============================
    public static class User_Bmp extends Structure {
        public int x;
        public int y;
        public int width;
        public int height;
        public int playStyle;
        public int playSpeed;
        public int delayTime;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("x","y","width","height",
                    "playStyle","playSpeed","delayTime");
        }
    }


    // =============================
    //  TEXT MODEL
    // =============================
    public static class User_Text extends Structure {
        public int x;
        public int y;
        public int width;
        public int height;
        public int fontSize;
        public int color;
        public int speed;
        public int style;
        public String text;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("x","y","width","height",
                    "fontSize","color","speed","style","text");
        }
    }

    // =============================
    //  SINGLE LINE TEXT
    // =============================
    public static class User_SingleText extends Structure {
        public int x;
        public int y;
        public int width;
        public int height;
        public int fontSize;
        public int color;
        public int speed;
        public String text;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("x","y","width","height",
                    "fontSize","color","speed","text");
        }
    }

    // =============================
    //  TIME DISPLAY
    // =============================
    public static class User_DateTime extends Structure {
        public int x;
        public int y;
        public int width;
        public int height;
        public int fontSize;
        public int color;
        public int timeFormat;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("x","y","width","height",
                    "fontSize","color","timeFormat");
        }
    }

    // =============================
    //  COUNTDOWN TIMER
    // =============================
    public static class User_Timer extends Structure {
        public int x;
        public int y;
        public int width;
        public int height;
        public int fontSize;
        public int color;
        public int mode;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("x","y","width","height",
                    "fontSize","color","mode");
        }
    }

    // =============================
    //  TEMPERATURE
    // =============================
    public static class User_Temperature extends Structure {
        public int x;
        public int y;
        public int fontSize;
        public int color;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("x","y","fontSize","color");
        }
    }

    // =============================
    //  MOVE STYLE
    // =============================
    public static class User_MoveSet extends Structure {
        public int style;
        public int speed;
        public int delay;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("style","speed","delay");
        }
    }

    // =============================
    //  FONT SET
    // =============================
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
