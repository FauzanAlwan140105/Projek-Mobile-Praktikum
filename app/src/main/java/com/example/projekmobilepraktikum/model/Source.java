package com.example.projekmobilepraktikum.model;

import com.google.gson.annotations.SerializedName;

/**
 * Kelas model untuk sumber berita
 * Menyimpan informasi tentang penerbit artikel berita
 */
public class Source {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    // Getter dan setter
    public String getId() {
        return id;
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
}
