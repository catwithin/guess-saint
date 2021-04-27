package com.gamesofni.neko.guesswhichsaint.data;


import java.io.Serializable;

public class Painting implements Serializable {
    private long id;
    private long saintId;
    private String fileName;
    private Integer resourceName;
    private String name;
    private String author;
    private String wikiLink;
    private String explanation;
    private Integer correctCount;

    public Painting(long id, Integer resourceName, Integer correctCount) {
        this.id = id;
        this.resourceName = resourceName;
        this.correctCount = correctCount;
    }

    public Painting(long id, String name, Integer resourceName) {
        this.id = id;
        this.name = name;
        this.resourceName = resourceName;
    }

    public Painting(Long id, Integer resourceName, String paintingName, Integer correctCount, long saintId) {
        this.id = id;
        this.resourceName = resourceName;
        this.correctCount = correctCount;
        this.saintId = saintId;
        this.name = paintingName;
    }

    public Painting(long id) {
        this.id = id;
    }

    // only used to remove painting from unguessed list based on id
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof Painting)) return false;
        Painting p = (Painting) obj;
        return p.id == this.id;
    }

    @Override
    public String toString() {
        return "Painting{" +
                "id=" + id +
                ", saintId=" + saintId +
                ", fileName='" + fileName + '\'' +
                ", resourceName=" + resourceName +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", wikiLink='" + wikiLink + '\'' +
                ", explanation='" + explanation + '\'' +
                ", correctCount=" + correctCount +
                '}';
    }

    public Integer getCorrectCount() {
        return correctCount;
    }

    public Integer getResourceName() {
        return resourceName;
    }

    public long getId() {
        return id;
    }

    public long getSaintId() {
        return saintId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getWikiLink() {
        return wikiLink;
    }

    public String getExplanation() {
        return explanation;
    }
}
