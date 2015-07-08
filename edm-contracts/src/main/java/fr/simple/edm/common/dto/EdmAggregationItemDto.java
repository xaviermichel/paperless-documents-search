package fr.simple.edm.common.dto;

public class EdmAggregationItemDto {
    
    private String key;
    
    private long docCount;

    public EdmAggregationItemDto() {
    }
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getDocCount() {
        return docCount;
    }

    public void setDocCount(long docCount) {
        this.docCount = docCount;
    }
}
