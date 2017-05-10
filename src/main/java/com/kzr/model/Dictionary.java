package com.kzr.model;

import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Created by Kamil on 2017-04-26.
 */

@Component
public class Dictionary {

    private Set<String> words;

    public Set<String> getWords() {
        return words;
    }

    public void setWords(Set<String> words) {
        this.words = words;
    }





}
