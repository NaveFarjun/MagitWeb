package utils;

public class WCFile {
    private boolean isDirectory;
    private String content;

    public WCFile(boolean isDirectory, String content) {
        this.isDirectory = isDirectory;
        this.content = content;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
