package org.example.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;

@Entity
@Table (name = "country_language", schema = "world")
@Getter
@Setter
public class CountryLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne (cascade = CascadeType.ALL)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Column(columnDefinition = "default ''", nullable = false)
    private String language;

    @Column(name = "is_official", columnDefinition = "BIT", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isOfficial;

    @Column(precision = 4, scale = 1, columnDefinition = "default 0.0", nullable = false)
    private BigDecimal percentage;



}
