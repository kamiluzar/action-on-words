package com.kzr.controller;

import com.kzr.service.DictionarySerivce;
import com.kzr.service.InformalWordsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

/**
 * Created by Kamil on 2017-04-26.
 */

/**
 *  Class responsible for processing user requests and building an appropriate model and passes it to the view for rendering.
 */
@Controller
public class WordsController {

    private final DictionarySerivce dictionarySerivce;
    private final InformalWordsService informalWordsService;

    public WordsController(DictionarySerivce dictionarySerivce, InformalWordsService informalWordsService) {
        this.dictionarySerivce = dictionarySerivce;
        this.informalWordsService = informalWordsService;
    }

    /**
     * This method returns the html file with home page
     */
    @RequestMapping("/")
    public String home() {
        return "home";
    }

    /**
     * This method returns the html file with the requested antonyms
     * @param word Search word
     * @param model Passing data to view
     */
    @RequestMapping(value = "/check-antonym", method = RequestMethod.POST)
    public String checkAntonym (@RequestParam(value = "message") String word, Model model) throws IOException {
        String result = dictionarySerivce.findAntonyms(word);
        model.addAttribute("result", result);
        model.addAttribute("word", word);
        return "home";
    }

    /**
     * This method returns the html file with information about the formality of the word
     * @param text Search word
     * @param model Passing data to view
     */
    @RequestMapping(value = "/formal-list", method = RequestMethod.POST)
    public String checkFormal (@RequestParam(value = "message") String text, Model model) throws IOException {
        String result = informalWordsService.isFormal(text);
        model.addAttribute("result", result);
        return "home";
    }
}



