package org.example.service;

import org.example.domain.UrlMapping;
import org.example.repository.UrlShortenerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class UrlShortenerService {

    @Autowired
    private UrlShortenerRepository urlShortenerRepository;

    // url 단축
    public String shortening(String longUrl) throws NoSuchAlgorithmException {
        UrlMapping urlSet = new UrlMapping();
        urlSet.setLongUrl(longUrl);

        String shortUrl = getHash(longUrl);
        urlSet.setShortUrl(shortUrl);

        urlShortenerRepository.save(urlSet);

        return shortUrl;
    }

    // 해시 값 생성
    private String getHash(String longUrl) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(longUrl.getBytes());
        byte[] digest = md.digest();

        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }

        return sb.toString().substring(0, 6);
    }

    // url 조회
    public String getLongUrl(String shortUrl) {
        UrlMapping urlMapping = urlShortenerRepository.findByShortUrl(shortUrl);
        return urlMapping.getLongUrl();
    }

    // 단축 url 삭제
    public Boolean deleteUrl(String shortUrl) {
        UrlMapping urlMapping = urlShortenerRepository.findByShortUrl(shortUrl);
        urlShortenerRepository.delete(urlMapping);

        return true;
    }
}
