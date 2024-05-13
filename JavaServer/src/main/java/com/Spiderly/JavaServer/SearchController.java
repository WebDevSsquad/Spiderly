package com.Spiderly.JavaServer;

import Models.Message;
import SearchHandler.SearchHandler;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class SearchController {
    @GetMapping("/search/{query}")
    public Map<String, Object> search(@PathVariable String query){
//        HashMap<String,Integer> m = new HashMap<>();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("name", "John");
//        jsonObject.put("age", 30);
//        jsonObject.put("isStudent", true);
//        m.put("1",2);
//        m.put("2",3);
//        m.put(query,4);
        return new SearchHandler().searchQuery(query);
    }

}
