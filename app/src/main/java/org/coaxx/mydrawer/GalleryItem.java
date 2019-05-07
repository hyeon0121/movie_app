package org.coaxx.mydrawer;

public class GalleryItem {
    String url;
    boolean val;

    public GalleryItem(String url, boolean val) {
        this.url = url;
        this.val = val;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isVal() {
        return val;
    }

    public void setVal(boolean val) {
        this.val = val;
    }
}
