package org.example.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.example.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@Controller
public class UrlShortnerController {

    @Autowired
    private UrlShortenerService urlShortenerService;

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect (@PathVariable String shortUrl, HttpServletResponse response) throws IOException {
        String longUrl = urlShortenerService.getLongUrl(shortUrl);
        if (longUrl != null) {
            response.sendRedirect(longUrl);
            return ResponseEntity.status(HttpStatus.FOUND).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
