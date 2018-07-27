package com.gamesofni.neko.guesswhichsaint.data;


public class Painting {
    private long id;
    private long saintId;
    private String fileName;
    private Integer resourceName;
    private String name;
    private String author;
    private String wikiLink;
    private String explanation;
    private Integer correctCount;

    public Painting(Long id, Integer resourceName, Integer correctCount) {
        this.id = id;
        this.resourceName = resourceName;
        this.correctCount = correctCount;
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
