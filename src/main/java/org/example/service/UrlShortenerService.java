package org.example.service;

import org.example.domain.UrlMapping;
import org.example.repository.UrlShortenerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UrlShortenerService {

    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private int counter = 0; // 카운터 초기화

    private static final int URL_EXPIRATION_DAYS = 30; // 만료 기간 설정
    private static final int INACTIVITY_DAYS = 7; // 비활성 기간 설정

    @Autowired
    private static UrlShortenerRepository urlShortenerRepository;

    // url 단축
    public String generate(String longUrl) throws NoSuchAlgorithmException {
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
        byte[] digest = md.digest(); // 16바이트 해시 값 생성

        // MD5 해시를 숫자로 변환
        long hashValue = 0;
        for (int i = 0; i < digest.length; i++) {
            hashValue = (hashValue << 8) | (digest[i] & 0xff);
        }

        // Base62 인코딩으로 변환하여 짧은 URL 생성
        StringBuilder shortUrl = new StringBuilder();
        while (hashValue > 0) {
            shortUrl.append(BASE62.charAt((int) (hashValue % 62)));
            hashValue /= 62;
        }

        // 6자리가 안 될 경우 대비. 최소 6자리가 되도록 패딩 처리
        while (shortUrl.length() < 6) {
            shortUrl.append('a');
        }

        // 6자리로 자름
        String sixShortUrl = shortUrl.toString().substring(0, 6);

        // 충돌 해결을 위한 카운터 사용
        while (urlShortenerRepository.existsByShortUrl(sixShortUrl)) {
            // 카운터 값을 추가하여 새로운 짧은 URL 생성
            sixShortUrl = shortUrl.toString().substring(0, 6) + Integer.toString(counter++);
            if (counter > 9999) { // 카운터가 너무 커지면 방지 로직 추가
                throw new RuntimeException("Unique URL generation failed");
            }
        }

        return sixShortUrl;

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

    public String createOrRetrieveShortUrl(String longUrl) throws NoSuchAlgorithmException {
        String shortUrl = generate(longUrl);

        LocalDateTime now = LocalDateTime.now();
        UrlMapping existingUrl = urlShortenerRepository.findByShortUrlAndExpiresAtAfter(shortUrl, now);

        if (existingUrl != null) {
            // URL이 존재하고 유효하면 반환
            UrlMapping urlEntity = existingUrl;
            urlEntity.setLastAccessed(now);
            urlShortenerRepository.save(urlEntity);
            return shortUrl;
        }

        // URL이 존재하지 않거나 만료된 경우, 새로운 URL을 생성
        UrlMapping newUrlMapping = new UrlMapping();
        newUrlMapping.setLongUrl(longUrl);
        newUrlMapping.setShortUrl(shortUrl);
        newUrlMapping.setCreatedAt(now);
        newUrlMapping.setLastAccessed(now);
        newUrlMapping.setExpiresAt(now.plusDays(URL_EXPIRATION_DAYS));

        urlShortenerRepository.save(newUrlMapping);
        return shortUrl;
    }

    public static void cleanupExpiredUrls() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.minusDays(INACTIVITY_DAYS);

        List<UrlMapping> expiredOrInactiveUrls = urlShortenerRepository.findExpiredOrInactiveUrls(now, threshold);
        urlShortenerRepository.deleteAll(expiredOrInactiveUrls);
    }
}
