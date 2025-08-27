package com.amay.printer.Response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImagePrintResponse extends BaseResponse {
    List<String> imagesName;
   public ImagePrintResponse(){
        imagesName=new ArrayList<>();
    }
}
