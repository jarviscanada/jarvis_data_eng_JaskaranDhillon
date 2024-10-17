package ca.jrvs.apps.trading.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "trader")
public class Trader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "first_name", nullable = false)
    @NotNull(message = "First name cannot be null")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotNull(message = "Last name cannot be null")
    private String lastName;

    @Column(name = "dob", nullable = false)
    @NotNull(message = "DOB cannot be null")
    private LocalDate dob;

    @Column(name = "country", nullable = false)
    @NotNull(message = "Country cannot be null")
    private String country;

    @Column(name = "email", nullable = false)
    @NotNull(message = "Email cannot be null")
    private String email;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}