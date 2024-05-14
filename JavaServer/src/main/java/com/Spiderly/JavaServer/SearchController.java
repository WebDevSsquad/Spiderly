package com.Spiderly.JavaServer;

import Models.Message;
import SearchHandler.SearchHandler;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SearchController {
    @CrossOrigin
    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam String q) throws IOException, ParseException {
//        HashMap<String,Integer> m = new HashMap<>();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("name", "John");
//        jsonObject.put("age", 30);
//        jsonObject.put("isStudent", true);
//        m.put("1",2);
//        m.put("2",3);
//        m.put(query,4);
        return new SearchHandler().searchQuery(q);
    }

    @CrossOrigin
    @GetMapping("/suggestions")
    public Map<String, Object> suggestions(){
        return new SearchHandler().getSuggestions();
    }

}
