package com.example.assistio.model;
import jakarta.persistence.*;
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false, unique = true)
    private String name;
    private String services; 
    public Role() {}
    public Role(String name, String reviews, String services, String payments) {
        this.name = name;
        this.services = services;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getServiceAccess(){
        return services;
    }
    public void setServiceAccess(String services){
        this.services = services;
    }
}
