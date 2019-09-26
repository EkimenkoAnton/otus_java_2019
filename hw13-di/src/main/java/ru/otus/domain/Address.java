package ru.otus.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String street;

    @JsonIgnore
    @EqualsAndHashCode.Exclude @ToString.Exclude
    @OneToOne(optional = false, mappedBy = "address", fetch = FetchType.EAGER)
    private User user;
}