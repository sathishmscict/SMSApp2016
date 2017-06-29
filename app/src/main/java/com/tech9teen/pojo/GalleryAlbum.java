package com.tech9teen.pojo;

/**
 * Created by Satish Gadde on 25-07-2016.
 */
public class GalleryAlbum {


    String description, classname;

    String thumbnail, delete;

    public GalleryAlbum(String description, String classname, String thumbnail, String delete) {
        this.description = description;
        this.classname = classname;
        this.thumbnail = thumbnail;
        this.delete = delete;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDelete() {
        return delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }




    public GalleryAlbum() {

    }


}
