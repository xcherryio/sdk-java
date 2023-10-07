package io.xdb.core.utils;

// mainly to return default values for null cases
public class ProcessUtil {

    public static String getClassSimpleName(final Class<?> obejctClass) {
        return obejctClass.getSimpleName();
    }
}
