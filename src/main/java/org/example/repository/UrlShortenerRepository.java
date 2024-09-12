package org.example.repository;

import org.example.domain.UrlMapping;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlShortenerRepository extends CrudRepository<UrlMapping, String> {
    UrlMapping findByShortUrl(String shortUrl);

    Boolean existsByShortUrl(String shortUrl);
}
