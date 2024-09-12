package org.example.repository;

import org.example.domain.UrlMapping;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlShortenerRepository extends CrudRepository<UrlMapping, String> {
    UrlMapping findByShortUrl(String shortUrl);

    Boolean existsByShortUrl(String shortUrl);

    UrlMapping findByShortUrlAndExpiresAtAfter(String shortUrl, LocalDateTime now);

    @Query("SELECT u FROM UrlMapping u WHERE u.expiresAt < :now OR u.lastAccessed < :threshold")
    List<UrlMapping> findExpiredOrInactiveUrls(LocalDateTime now, LocalDateTime threshold);
}
