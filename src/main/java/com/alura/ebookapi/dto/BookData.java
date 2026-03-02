package com.alura.ebookapi.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BookData {

    private Integer id;
    private String title;
    private List<AuthorData> authors;
    private List<String> languages;

    @JsonAlias("download_count")
    private Integer downloadCount;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<AuthorData> getAuthors() { return authors; }
    public void setAuthors(List<AuthorData> authors) { this.authors = authors; }

    public List<String> getLanguages() { return languages; }
    public void setLanguages(List<String> languages) { this.languages = languages; }

    public Integer getDownloadCount() { return downloadCount; }
    public void setDownloadCount(Integer downloadCount) { this.downloadCount = downloadCount; }
}
