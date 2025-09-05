package com.amay.tom.enums;

import lombok.Getter;

@Getter
public enum Language {
    HINDI("Hindi",1),
    ENGLISH("English",0);


    private final String languageName;
    private final int languageId;

    Language(String languageName, int languageId) {
        this.languageName = languageName;
        this.languageId = languageId;
    }

}
