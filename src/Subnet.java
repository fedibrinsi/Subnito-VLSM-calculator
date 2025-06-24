public class Subnet {
    private String name;
    private String addresseReseau;
    private String addresseBroadcast;
    private String premAddUtilisable;
    private String dernAddUtilisable;
    private int masque;
    private int hotes;
    
    public Subnet(String name, int hotes) {
        setName(name);
        setHotes(hotes);
    }
    
    // Getters
    public String getName() {
        return name;
    }
    
    public String getAddresseReseau() {
        return addresseReseau;
    }
    
    public String getAddresseBroadcast() {
        return addresseBroadcast;
    }
    
    public String getPremAddUtilisable() {
        return premAddUtilisable;
    }
    
    public String getDernAddUtilisable() {
        return dernAddUtilisable;
    }
    
    public int getMasque() {
        return masque;
    }
    
    public int getHotes() {
        return hotes;
    }
    
    // Setters
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du subnet ne peut pas être null ou vide.");
        }
        this.name = name.trim();
    }
    
    public void setAddresseReseau(String addresseReseau) {
        if (addresseReseau == null || !isValidIpAddress(addresseReseau)) {
            throw new IllegalArgumentException("Adresse réseau invalide: " + addresseReseau);
        }
        this.addresseReseau = addresseReseau.trim();
    }
    
    public void setAddresseBroadcast(String addresseBroadcast) {
        if (addresseBroadcast == null || !isValidIpAddress(addresseBroadcast)) {
            throw new IllegalArgumentException("Adresse broadcast invalide: " + addresseBroadcast);
        }
        this.addresseBroadcast = addresseBroadcast.trim();
    }
    
    public void setPremAddUtilisable(String premAddUtilisable) {
        if (premAddUtilisable == null || !isValidIpAddress(premAddUtilisable)) {
            throw new IllegalArgumentException("Première adresse utilisable invalide: " + premAddUtilisable);
        }
        this.premAddUtilisable = premAddUtilisable.trim();
    }
    
    public void setDernAddUtilisable(String dernAddUtilisable) {
        if (dernAddUtilisable == null || !isValidIpAddress(dernAddUtilisable)) {
            throw new IllegalArgumentException("Dernière adresse utilisable invalide: " + dernAddUtilisable);
        }
        this.dernAddUtilisable = dernAddUtilisable.trim();
    }
    
    public void setMasque(int masque) {
        if (masque < 0 || masque > 32) {
            throw new IllegalArgumentException("Masque invalide: " + masque + ". Doit être entre 0 et 32.");
        }
        this.masque = masque;
    }
    
    public void setHotes(int hotes) {
        if (hotes <= 0) {
            throw new IllegalArgumentException("Le nombre d'hôtes doit être positif. Valeur donnée: " + hotes);
        }
        if (hotes > 1073741822) { // 2^30 - 2 (maximum theoretical hosts)
            throw new IllegalArgumentException("Le nombre d'hôtes est trop grand. Maximum théorique: 1073741822");
        }
        this.hotes = hotes;
    }

    private boolean isValidIpAddress(String ip) {
        if (ip == null) return false;
        String[] parts = ip.split("\\.");
        if (parts.length != 4) return false;
        try {
            for (String part : parts) {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return String.format("Subnet{name='%s', hotes=%d, addresseReseau='%s', masque=%d}", 
                           name, hotes, addresseReseau, masque);
    }
}