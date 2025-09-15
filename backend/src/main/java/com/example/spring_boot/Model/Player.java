package com.example.spring_boot.Model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;

/**
 * Define a user data structure
 */
@Entity
@Table(name = "users")
public class Player implements UserDetails {

    //Primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    //attributes
    private String username;
    private String password;


    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Role role; 



    //Constructors
    public Player(){}

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
    }

    //Getters and setters
    public Long getId() {return this.id;}
    protected void setId(Long id){this.id = id;}

    public String getUsername(){return this.username;}
    public void setUsername(String username){this.username = username;}

    public Role getRole(){return this.role;}
    public void setRole(Role role){this.role = role;}

    public String getPassword(){return this.password;}
    public void setPassword(String password){
        this.password = password;
    }

    //return a list of the users privledges
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}
