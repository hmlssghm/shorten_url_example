package org.example.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.example.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Controller
public class UrlShortnerController {

    @Autowired
    private UrlShortenerService urlShortenerService;

    // url 단축
    @PostMapping("/shortening")
    public ResponseEntity<String> generate (@RequestBody String longUrl) throws NoSuchAlgorithmException {
        String shortUrl = urlShortenerService.generate(longUrl);
        if (shortUrl != null) {
            return new ResponseEntity<>(shortUrl, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // url 조회
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

    // url 삭제
    @DeleteMapping("/{shortUrl}")
    public ResponseEntity<String> delete (@PathVariable String shortUrl) {
        Boolean deleted = urlShortenerService.deleteUrl(shortUrl);
        if (deleted) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }
}
