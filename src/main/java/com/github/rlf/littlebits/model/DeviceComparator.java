package com.github.rlf.littlebits.model;

import java.util.Comparator;

public class DeviceComparator implements Comparator<Device> {
    @Override
    public int compare(Device o1, Device o2) {
        String s1 = o1 != null && o1.getLabel() != null ? o1.getLabel() : o1 != null ? o1.getId() : "";
        String s2 = o2 != null && o2.getLabel() != null ? o2.getLabel() : o2 != null ? o2.getId() : "";
        return s1.compareTo(s2);
    }
}
