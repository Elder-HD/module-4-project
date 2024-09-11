package org.example.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "country", schema = "world")
@Getter
@Setter
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "default ''", nullable = false)
    private String code;

    @Column(name = "code_2", columnDefinition = "default ''", nullable = false)
    private String code2;

    @Column(columnDefinition = "default ''", nullable = false)
    private String name;

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "default 0", nullable = false)
    private Continent continent;

    @Column(columnDefinition = "default ''", nullable = false)
    private String region;

    @Column(name = "surface_area", columnDefinition = "default 0.00", precision = 10, scale = 2, nullable = false)
    private BigDecimal surfaceArea;

    @Column(name = "indep_year")
    private Short independenceYear;

    @Column(columnDefinition = "default 0", nullable = false)
    private Integer population;

    @Column(name = "life_expectancy", precision = 3, scale = 1)
    private BigDecimal lifeExpectancy;

    @Column(name = "gnp", precision = 10, scale = 2)
    private BigDecimal GNP;

    @Column(name = "gnpo_id",  precision = 10, scale = 2)
    private BigDecimal GNPOId;

    @Column(name = "local_name", columnDefinition = "default ''", nullable = false)
    private String localName;

    @Column(name = "government_form", columnDefinition = "default ''", nullable = false)
    private String governmentForm;

    @Column(name = "head_of_state")
    private String headOfState;

    @OneToOne(cascade = CascadeType.ALL)
    private City capital;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    private Set<CountryLanguage> languages;
}
