package com.gamesofni.neko.guesswhichsaint.data;

import java.util.ArrayList;
import java.util.List;


public class Saint {
    private long id;
    private String name;
    private List<Integer> paintings;
    private ArrayList<String> attributes;
    private int icon;
    private String info;
    private String wikiUrl;
    private Integer gender;
    private String category;


    public Saint(long id, String name, List<Integer> paintings, ArrayList<String> attributes, int icon, String info, String wikiUrl, Integer gender, String category) {
        this.id = id;
        this.name = name;
        this.paintings = paintings;
        this.attributes = attributes;
        this.icon = icon;
        this.info = info;
        this.wikiUrl = wikiUrl;
        this.gender = gender;
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getPaintings() {
        return paintings;
    }

    public ArrayList<String> getAttributes() {
        return attributes;
    }

    public int getIcon() {
        return icon;
    }

    public String getInfo() {
        return info;
    }

    public String getWikiUrl() {
        return wikiUrl;
    }

    public Integer getGender() {
        return gender;
    }

    public String getCategory() {
        return category;
    }
}
