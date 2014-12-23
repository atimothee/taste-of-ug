package com.tasteofuganda.backend;

import com.google.api.server.spi.config.ApiTransformer;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by Timo on 12/23/14.
 */
@Entity
//@ApiTransformer(CategoryTransformer.class)
public class Category{
    @Id Long id;
    String name;
    String color;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
