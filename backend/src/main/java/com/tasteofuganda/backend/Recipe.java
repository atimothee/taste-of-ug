package com.tasteofuganda.backend;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.api.server.spi.config.ApiTransformer;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.List;

/**
 * Created by Timo on 12/23/14.
 */
@Entity

public class Recipe {
    @Id
    Long id;
    String name;
    String description;
    String ingredients;
    Text directions;
    Link youtube_url;

    Key<Category> category;
    BlobKey image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Text getDirections() {
        return directions;
    }

    public void setDirections(Text directions) {
        this.directions = directions;
    }

    public Link getYoutube_url() {
        return youtube_url;
    }

    public void setYoutube_url(Link youtube_url) {
        this.youtube_url = youtube_url;
    }

    public BlobKey getImage() {
        return image;
    }

    public void setImage(BlobKey image) {
        this.image = image;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<Category> getCategory() {
        return category;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setCategory(Key<Category> category) {
        this.category = category;
    }

    public Long getCategoryId(){
        return category == null ? null : category.getId();
    }

    public void setCategoryId(Long categoryId){
        category = Key.create(Category.class, categoryId);
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
}


