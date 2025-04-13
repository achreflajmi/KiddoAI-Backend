    package com.example.kiddoai.Entities;

    import org.springframework.data.annotation.Id;
    import org.springframework.data.mongodb.core.mapping.Document;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.userdetails.UserDetails;
    import java.time.LocalDateTime;

    import java.util.Collection;
    import java.util.List;

    @Document(collection = "users") // Nom de la collection dans MongoDB
    public class User implements UserDetails {

        @Id
        private String id;
        private String nom;
        private String prenom;
        private String dateNaissance;
        private String email;
        private String password;
        private String IQCategory; // Changed from float niveauIQ to String IQCategory
        private float scoreTotal;
        private String favoriteCharacter;
        private String threadId;
        private String parentPhoneNumber; // new field
        private String lastProblemType; // e.g. "bullying", "danger", "none", etc.
        private LocalDateTime lastProblemTimestamp; // Add this field
        private String classe; // e.g. "1st", "2nd", ..., "6th"
        private Role role;

        // Constructeur par défaut
        public User() {}

        public User(String id, String nom, String prenom, String dateNaissance, String email, String password, String IQCategory, float scoreTotal, String favoriteCharacter, String threadId) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
            this.dateNaissance = dateNaissance;
            this.email = email;
            this.password = password;
            this.IQCategory = IQCategory;
            this.scoreTotal = scoreTotal;
            this.favoriteCharacter = favoriteCharacter;
            this.threadId = threadId;
        }

        public User(String nom, String prenom, String dateNaissance, String email, String password, int i, int i1, String favoriteCharacter, String s) {
        }

        // Getters et Setters
        public String getId() {
            return id;
        }


        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }

        public void setId(String id) {
            this.id = id;
        }
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
        public String getClasse() {
            return classe;
        }
        public void setClasse(String classe) {
            this.classe = classe;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getIQCategory() {
            return IQCategory;
        }

        public void setIQCategory(String IQCategory) {
            this.IQCategory = IQCategory;
        }
        public float getScoreTotal() {
            return scoreTotal;
        }

        public void setScoreTotal(float scoreTotal) {
            this.scoreTotal = scoreTotal;
        }

        public String getFavoriteCharacter() {
            return favoriteCharacter;
        }

        public void setFavoriteCharacter(String favoriteCharacter) {
            this.favoriteCharacter = favoriteCharacter;
        }

        public String getThreadId() {
            return threadId;
        }

        public void setThreadId(String threadId) {
            this.threadId = threadId;
        }

        public String getParentPhoneNumber() {
            return parentPhoneNumber;
        }
        public void setParentPhoneNumber(String parentPhoneNumber) {
            this.parentPhoneNumber = parentPhoneNumber;
        }
        public String getLastProblemType() {
            return lastProblemType;
        }

        public void setLastProblemType(String lastProblemType) {
            this.lastProblemType = lastProblemType;
        }
        public LocalDateTime getLastProblemTimestamp() {
            return lastProblemTimestamp;
        }

        public void setLastProblemTimestamp(LocalDateTime lastProblemTimestamp) {
            this.lastProblemTimestamp = lastProblemTimestamp;
        }
        // Méthode toString
        @Override
        public String toString() {
            return "User{" +
                    "id='" + id + '\'' +
                    ", nom='" + nom + '\'' +
                    ", prenom='" + prenom + '\'' +
                    ", dateNaissance='" + dateNaissance + '\'' +
                    ", email='" + email + '\'' +
                    ", password='" + password + '\'' +
                    ", niveauIQ=" + IQCategory +
                    ", scoreTotal=" + scoreTotal +
                    ", favoriteCharacter='" + favoriteCharacter + '\'' +
                    ", threadId='" + threadId + '\'' +
                    '}';
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(); // Aucune autorité pour l'instant
        }

        @Override
        public String getUsername() {
            return email; // Email comme username
        }

        @Override
        public boolean isAccountNonExpired() {
            return true; // Compte non expiré
        }

        @Override
        public boolean isAccountNonLocked() {
            return true; // Compte non bloqué
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true; // Credentials non expirés
        }

        @Override
        public boolean isEnabled() {
            return true; // Compte activé
        }
    }
