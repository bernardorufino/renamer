package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static <T> List<T> filter(List<T> list, Filter<T> filter) {
        List<T> filtered = new ArrayList<T>();
        for (T member : list) if (filter.filter(member)) filtered.add(member);
        return filtered;
    }
    
    public static <T> List<T> filter(List<T> list, Filter<T>... filters) {
        List<T> filtered = list;
        for (Filter<T> filter : filters) filtered = filter(filtered, filter);
        return filtered;
    }

}
