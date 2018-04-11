package melo;
public class Status {
    public static enum CopyMode {
        COPY, NOCOPY
    }
    public void print() {
        Status.CopyMode m = Status.CopyMode.COPY;
    }
}
