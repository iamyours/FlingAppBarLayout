package io.github.iamyours.flingappbarlayout;

import java.lang.reflect.Field;

public class ReflectUtil {
    public static Field getDeclaredField(Object object, String fieldName){
        Field field = null ;

        Class<?> clazz = object.getClass() ;

        for(; clazz != Object.class ; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName) ;
                return field ;
            } catch (Exception e) {

            }
        }
        return null;
    }
}
