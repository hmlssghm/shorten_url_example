package org.example.service;

import org.example.domain.UrlMapping;
import org.example.repository.UrlShortenerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlShortenerService {

    @Autowired
    private UrlShortenerRepository urlShortenerRepository;

    public String getLongUrl(String shortUrl) {
        UrlMapping urlMapping = urlShortenerRepository.findByShortUrl(shortUrl);
        return urlMapping.getLongUrl();
    }
}
