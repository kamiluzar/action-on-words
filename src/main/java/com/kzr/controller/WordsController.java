package com.kzr.controller;

import com.kzr.service.DictionarySerivce;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by Kamil on 2017-04-26.
 */

@Controller
public class WordsController {

    private final DictionarySerivce dictionarySerivce;

    public WordsController(DictionarySerivce dictionarySerivce) {
        this.dictionarySerivce = dictionarySerivce;
    }

    @RequestMapping("/")
    public String home() {
        return "home";
    }

    @RequestMapping(value = "/check-antonym", method = RequestMethod.POST)
    public String checkAntonym (@RequestParam(value = "message") String word, Model model) {
    String result = dictionarySerivce.findAntonyms(word);
    model.addAttribute("result", result);
    model.addAttribute("word", word);
        return "home";
    }
}



