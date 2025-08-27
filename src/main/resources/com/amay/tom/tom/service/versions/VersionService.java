package com.amay.tom.service.versions;

import com.amay.tom.config.dto.MasterConfigInfoCheck;
import com.amay.tom.config.dto.MasterConfigInfoDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.reflect.Field;
import java.util.Objects;

@Setter
@Getter
@ToString
public class VersionService {

    private MasterConfigInfoCheck masterConfigInfoCheck=null;
    private MasterConfigInfoDTO expected, actual;

    public MasterConfigInfoCheck compareVersion(MasterConfigInfoDTO expected,
                                                MasterConfigInfoDTO actual) {

        this.expected = expected;
        this.actual = actual;
        this.masterConfigInfoCheck = new MasterConfigInfoCheck();
        if(expected == null || actual == null) {
            return masterConfigInfoCheck;
        }


        //TODO: fix this to use a more robust comparison
        for (Field field : MasterConfigInfoDTO.class.getDeclaredFields()) {
            String name = field.getName();   // e.g. "tomConfig"
            field.setAccessible(true);
            try {
                Object v1 = field.get(expected);
                Object v2 = field.get(actual);
                boolean equal = Objects.equals(v1, v2);
                // build setter name on MasterConfigInfoCheck: setTomConfig(...)
                String setterName = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
                MasterConfigInfoCheck.class
                        .getMethod(setterName, boolean.class)
                        .invoke(masterConfigInfoCheck, equal);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to evaluate field " + name, e);
            }
        }

        //fetch software version



        return masterConfigInfoCheck;
    }
}
