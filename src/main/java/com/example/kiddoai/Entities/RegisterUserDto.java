package com.example.kiddoai.Entities;

public class RegisterUserDto {
    private String nom;
    private String prenom;
    private String dateNaissance;
    private String email;
    private String password;
    private String favoriteCharacter;
    private String parentPhoneNumber;

    // Constructors
    public RegisterUserDto() {}

    public RegisterUserDto(String nom, String prenom, String dateNaissance, String email, String password, float niveauIQ, String favoriteCharacter) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.email = email;
        this.password = password;
        this.favoriteCharacter = favoriteCharacter;
    }

    // Getters and Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public String getFavoriteCharacter() {
        return favoriteCharacter;
    }

    public void setFavoriteCharacter(String favoriteCharacter) {
        this.favoriteCharacter = favoriteCharacter;
    }

    public String getParentPhoneNumber() {
        return parentPhoneNumber;
    }
    public void setParentPhoneNumber(String parentPhoneNumber) {
        this.parentPhoneNumber = parentPhoneNumber;
    }

    @Override
    public String toString() {
        return "RegisterUserDto{" +
                "nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", dateNaissance='" + dateNaissance + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTECTED]" +

                ", favoriteCharacter='" + favoriteCharacter + '\'' +
                '}';
    }
}
