package com.example.chatapp.Model;

public class User {
    private String id;
    private String name;
    private String imageURL;
    private String status;
    private String search;

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User() {
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public User(String id, String name, String imageURL,String status,String search) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
        this.status=status;
        this.search=search;
    }
}
