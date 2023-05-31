package com.rest.api.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String name;
    private String job;
    private String email;
    private String number;
    private String company;
    private int age;
    private String city;

    public User(){

    }

    public User(String name, String job, String email, String number, int age, String company, String city){
        this.name = name;
        this.email = email;
        this.number = number;
        this.job = job;
        this.age = age;
        this.company = company;
        this.city = city;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() { return number; }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getJob() { return job; }

    public void setJob(String job) {
        this.job = job;
    }

    public String getCompany() { return company; }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCity() { return city; }

    public void setCity(String city) {
        this.city = city;
    }


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
