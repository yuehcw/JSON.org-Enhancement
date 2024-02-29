package org.json;

public class JSONNode {
    private String path;
    private String key;
    private Object value;

    public JSONNode(String path, String key, Object value) {
        this.path = path;
        this.key = key;
        this.value = value;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "JSONNode{" +
                "path='" + path + '\'' +
                ", key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}
