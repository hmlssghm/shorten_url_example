package org.example.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table
public class UrlMapping {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shortUrl;

    private String longUrl;

    @CreatedDate
    private LocalDateTime created_at;

    private LocalDateTime last_accessed;

    private LocalDateTime expires_at;
}
