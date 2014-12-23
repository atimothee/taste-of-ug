package com.tasteofuganda.backend;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by Timo on 12/23/14.
 */
@Entity
public class Review {
    @Id
    Long id;
    String comment;
    Key<User> user;
    Key<Recipe> recipe;
    int rating;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<User> getUser() {
        return user;
    }
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setUser(Key<User> user) {
        this.user = user;
    }
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<Recipe> getRecipe() {
        return recipe;
    }
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setRecipe(Key<Recipe> recipe) {
        this.recipe = recipe;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Long getUserId(){
        return user == null ? null : user.getId();
    }

    public void setUserId(Long userId){
        user = Key.create(User.class, userId);
    }

    public Long getRecipeId(){
        return recipe == null? null: recipe.getId();
    }

    public void setRecipeId(Long recipeId){
        recipe = Key.create(Recipe.class, recipeId);
    }
}
