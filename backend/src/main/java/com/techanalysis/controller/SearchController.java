package com.techanalysis.controller;

import com.techanalysis.dto.SearchResult;
import com.techanalysis.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<SearchResult> search(@RequestParam String keyword) {
        SearchResult result = searchService.search(keyword);
        return ResponseEntity.ok(result);
    }
}
