package org.example.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "city", schema = "world")
@Getter
@Setter
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, columnDefinition = "default ''")
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Column(nullable = false, columnDefinition = "default ''")
    private String district;

    @Column(nullable = false, columnDefinition = "default 0")
    private Integer population;

}