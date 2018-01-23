package com.example.luc11u.sam.model;

public class Site {
    private int id;
    private String nom, adresse, categorie, resume;
    private double latitude, longitude;

    public Site(int id, String name, String adress, String category, String summary, double lati, double longi){
        setId(id);
        setNom(name);
        setAdresse(adress);
        setCategorie(category);
        setResume(summary);
        setLatitude(lati);
        setLongitude(longi);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String toString(){
        return "Site " + getId() + " : " + getNom() + " " + getAdresse() + " " + getLatitude() + " " + getLongitude() + " " + getResume() + " " + getCategorie();
    }
}
