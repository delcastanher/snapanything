package com.github.delcastanher.snapanything;

import java.util.ArrayList;
import java.util.List;

public abstract class SnapTwitter {

    protected MyTwitter myTwitter = new MyTwitter();
    protected List<String> deletedStatuses = new ArrayList<String>();

    public List<String> getDeletedStatuses() {
        return deletedStatuses;
    }

}