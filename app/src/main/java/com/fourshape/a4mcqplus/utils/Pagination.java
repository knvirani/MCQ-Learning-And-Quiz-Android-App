package com.fourshape.a4mcqplus.utils;

public class Pagination {

    private int offset;
    private int limit;

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void raiseOffset(){
        this.offset += this.limit;
    }

}
