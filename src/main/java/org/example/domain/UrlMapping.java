package org.example.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table
public class UrlMapping {
    @Id
    private Long id;

    private String shortUrl;

    private String longUrl;

    private LocalDateTime created_at;
}
