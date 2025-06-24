import java.util.Comparator;
import java.util.Scanner;
import java.util.Arrays;

/**
 * VLSM CLI Tool
 * This tool calculates Variable Length Subnet Masking (VLSM) for a given IP address and subnet mask.
 * It allows users to define custom subnet names and host requirements, ensuring unique names.
 * The tool validates inputs, handles errors gracefully, and provides detailed output.
 */

public class VlsmCli {
    private static final int MAX_SUBNETS = 1000;
    private static final int MAX_HOST_COUNT = 1073741822; // 2^30 - 2

    // Convert mask to IP address with validation
    static String convertMaskToIp(int mask) {
        if (mask < 0 || mask > 32) {
            throw new IllegalArgumentException("Masque invalide: " + mask + ". Doit être entre 0 et 32.");
        }
        
        if (mask == 0) {
            return "0.0.0.0";
        }
        
        long maskValue = 0xFFFFFFFFL << (32 - mask);
        maskValue &= 0xFFFFFFFFL; // Ensure it stays within 32-bit range
        
        return ((maskValue >> 24) & 0xFF) + "." +
               ((maskValue >> 16) & 0xFF) + "." +
               ((maskValue >> 8) & 0xFF) + "." +
               (maskValue & 0xFF);
    }

    //  IP address verification
    static boolean verif(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return false;
        }
        
        ip = ip.trim();
        String[] ipTab = ip.split("\\.");
        
        if (ipTab.length != 4) {
            return false;
        }
        
        try {
            for (String part : ipTab) {
                if (part.isEmpty()) {
                    return false;
                }
                
                // Check for leading zeros (except for "0")
                if (part.length() > 1 && part.startsWith("0")) {
                    return false;
                }
                
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //  IP increment function
    static String incrementIp(String ip, int increment) {
        if (ip == null || ip.trim().isEmpty()) {
            throw new IllegalArgumentException("Adresse IP ne peut pas être null ou vide.");
        }
        
        if (!verif(ip)) {
            throw new IllegalArgumentException("Format d'adresse IP invalide: " + ip);
        }

        // Split the IP address into octets
        String[] parts = ip.split("\\.");
        
        // Convert octets to long to handle large increments
        long ipAsLong = 0;
        for (int i = 0; i < 4; i++) {
            ipAsLong = (ipAsLong << 8) + Integer.parseInt(parts[i]);
        }

        // Apply increment
        long newIpAsLong = ipAsLong + increment;

        // Check bounds
        if (newIpAsLong < 0) {
            throw new IllegalArgumentException("Adresse IP underflow. Résultat: " + newIpAsLong);
        }
        if (newIpAsLong > 0xFFFFFFFFL) {
            throw new IllegalArgumentException("Adresse IP overflow. Résultat: " + newIpAsLong);
        }

        // Convert back to IP format
        return String.format("%d.%d.%d.%d",
                (newIpAsLong >> 24) & 0xFF,
                (newIpAsLong >> 16) & 0xFF,
                (newIpAsLong >> 8) & 0xFF,
                newIpAsLong & 0xFF);
    }

    // Check if an IP address is within a valid range for subnetting
    static boolean isValidNetworkAddress(String ip, int mask) {
        if (!verif(ip) || mask < 0 || mask > 32) {
            return false;
        }
        
        try {
            String[] parts = ip.split("\\.");
            long ipAsLong = 0;
            for (int i = 0; i < 4; i++) {
                ipAsLong = (ipAsLong << 8) + Integer.parseInt(parts[i]);
            }
            
            // Check if it's a network address
            if (mask == 32) return true;
            
            long networkMask = 0xFFFFFFFFL << (32 - mask);
            networkMask &= 0xFFFFFFFFL;
            
            return (ipAsLong & ~networkMask) == 0;
        } catch (Exception e) {
            return false;
        }
    }

    // Safe integer input with validation
    static int getSafeIntInput(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                
                if (input.isEmpty()) {
                    System.out.println("Entrée vide. Veuillez entrer un nombre.");
                    continue;
                }
                
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.println("Valeur hors limites. Doit être entre " + min + " et " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Format invalide. Veuillez entrer un nombre entier.");
            }
        }
    }

    // Safe string input with validation
    static String getSafeStringInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input != null && !input.trim().isEmpty()) {
                return input.trim();
            }
            System.out.println("Entrée vide. Veuillez entrer une valeur valide.");
        }
    }

    // Validate subnet name to avoid duplicates
    static boolean isSubnetNameUnique(String name, Subnet[] subnets, int currentIndex) {
        for (int i = 0; i < currentIndex; i++) {
            if (subnets[i] != null && subnets[i].getOriginalName().equalsIgnoreCase(name)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("=== Calculateur VLSM avec Noms Personnalisés ===\n");

            // Get IP address with validation
            String ip;
            while (true) {
                ip = getSafeStringInput(scanner, "Donner l'adresse IP que vous voulez subdiviser : ");
                if (verif(ip)) {
                    break;
                }
                System.out.println("Format d'adresse IP invalide. Format attendu: xxx.xxx.xxx.xxx (0-255 pour chaque octet)");
            }

            // Get subnet mask with validation
            int mask = getSafeIntInput(scanner, "Donner le masque de ce réseau (0-32) : ", 0, 32);

            // Validate that the IP is a proper network address
            if (!isValidNetworkAddress(ip, mask)) {
                System.out.println("Attention: L'adresse IP donnée pourrait ne pas être une adresse réseau valide pour le masque /" + mask);
                System.out.println("Continuation avec l'adresse donnée...\n");
            }

            // Get number of subnets with reasonable limits
            int n = getSafeIntInput(scanner, "Donner le nombre de subnets (1-" + MAX_SUBNETS + ") : ", 1, MAX_SUBNETS);
            
            Subnet[] subnetTab = new Subnet[n];

            // Initialize subnets with custom names and host requirements
            System.out.println("\n=== Configuration des Subnets ===");
            for (int i = 0; i < n; i++) {
                System.out.println("\n--- Configuration du Subnet " + (i + 1) + " ---");
                
                // Get custom subnet name
                String subnetName;
                while (true) {
                    subnetName = getSafeStringInput(scanner, "Nom du subnet " + (i + 1) + " : ");
                    if (isSubnetNameUnique(subnetName, subnetTab, i)) {
                        break;
                    }
                    System.out.println("Ce nom de subnet existe déjà. Veuillez choisir un nom unique.");
                }
                
                // Get number of hosts for this subnet
                while (true) {
                    try {
                        int hotes = getSafeIntInput(scanner, 
                            "Nombre d'hôtes pour '" + subnetName + "' (1-" + MAX_HOST_COUNT + ") : ", 
                            1, MAX_HOST_COUNT);
                        
                        subnetTab[i] = new Subnet(subnetName, hotes);
                        System.out.println("✓ Subnet '" + subnetName + "' configuré avec " + hotes + " hôtes.");
                        break;
                    } catch (IllegalArgumentException e) {
                        System.out.println("Erreur: " + e.getMessage());
                    }
                }
            }

            // Create a copy of the original order for display purposes
            Subnet[] originalOrder = new Subnet[n];
            for (int i = 0; i < n; i++) {
                originalOrder[i] = subnetTab[i];
            }

            // Sort subnets by number of hosts (descending order) for calculation
            Arrays.sort(subnetTab, Comparator.comparingInt(Subnet::getHosts).reversed());

            // Calculate total hosts needed
            long totalHostsNeeded = 0;
            for (Subnet subnet : subnetTab) {
                int hotes = subnet.getHosts();
                int hostBits = (int) Math.ceil(Math.log(hotes + 2) / Math.log(2));
                totalHostsNeeded += Math.pow(2, hostBits);
            }

            // Check if available address space is sufficient
            long availableAddresses = (long) Math.pow(2, 32 - mask);
            if (totalHostsNeeded > availableAddresses) {
                throw new IllegalArgumentException(
                    "Espace d'adressage insuffisant. Besoin: " + totalHostsNeeded + 
                    ", Disponible: " + availableAddresses + " adresses.");
            }

            System.out.println("\n=== Calcul des subnets (ordre par taille décroissante) ===");

            // Calculate and assign subnet details
            String currentIp = ip;
            for (int i = 0; i < subnetTab.length; i++) {
                Subnet subnet = subnetTab[i];
                try {
                    int hotes = subnet.getHosts();
                    int hostBits = (int) Math.ceil(Math.log(hotes + 2) / Math.log(2));
                    int subnetMask = 32 - hostBits;

                    // Validate subnet mask doesn't exceed original mask
                    if (subnetMask < mask) {
                        throw new IllegalArgumentException(
                            "Le subnet '" + subnet.getName() + "' nécessite un masque /" + subnetMask + 
                            " qui est moins restrictif que le masque original /" + mask);
                    }

                    int subnetSize = (int) Math.pow(2, hostBits);

                    // Network address
                    subnet.setAddresseReseau(currentIp);

                    // Broadcast address
                    String broadcastAddress = incrementIp(currentIp, subnetSize - 1);
                    subnet.setAddresseBroadcast(broadcastAddress);

                    // Usable IP range
                    String firstUsable = incrementIp(currentIp, 1);
                    String lastUsable = incrementIp(broadcastAddress, -1);
                    subnet.setPremAdd(firstUsable);
                    subnet.setDernAdd(lastUsable);

                    // Subnet mask
                    subnet.setMasque(subnetMask);

                    // Update current IP for the next subnet
                    currentIp = incrementIp(currentIp, subnetSize);

                    System.out.println("✓ Subnet '" + subnet.getName() + "' calculé avec succès.");

                } catch (Exception e) {
                    throw new RuntimeException("Erreur lors du calcul du subnet '" + subnet.getName() + "': " + e.getMessage(), e);
                }
            }

            // Print results in original order
            System.out.println("\n" + "=".repeat(60));
            System.out.println("              RÉSULTATS VLSM");
            System.out.println("=".repeat(60));
            System.out.println("Adresse réseau originale: " + ip + "/" + mask);
            System.out.println("Masque de sous-réseau: " + convertMaskToIp(mask));
            System.out.println("Nombre total de subnets: " + n);
            System.out.println("Total d'adresses utilisées: " + totalHostsNeeded + "/" + availableAddresses);
            System.out.println("=".repeat(60));

            // Display results in original input order
            System.out.println("\n=== Subnets dans l'ordre de saisie ===");
            for (int i = 0; i < originalOrder.length; i++) {
                Subnet subnet = originalOrder[i];
                System.out.println("\n┌─ " + subnet.getName() + " " + "─".repeat(Math.max(1, 45 - subnet.getName().length())));
                System.out.println("│ Hôtes demandés      : " + subnet.getHosts());
                System.out.println("│ Adresse Réseau      : " + subnet.getAddresseReseau());
                System.out.println("│ Adresse Broadcast   : " + subnet.getAddresseBroadcast());
                System.out.println("│ Première Utilisable : " + subnet.getPremAddUtilisable());
                System.out.println("│ Dernière Utilisable : " + subnet.getDernAddUtilisable());
                System.out.println("│ Masque              : /" + subnet.getMasque() + " (" + convertMaskToIp(subnet.getMasque()) + ")");
                
                int actualHosts = (int) Math.pow(2, 32 - subnet.getMasque()) - 2;
                System.out.println("│ Hôtes disponibles   : " + actualHosts);
                int wastedHosts = actualHosts - subnet.getHosts();
                if (wastedHosts > 0) {
                    System.out.println("│ Hôtes gaspillés     : " + wastedHosts);
                }
                System.out.println("└" + "─".repeat(50));
            }

            // Display summary by size order
            System.out.println("\n=== Récapitulatif par ordre de taille (plus grand au plus petit) ===");
            for (int i = 0; i < subnetTab.length; i++) {
                Subnet subnet = subnetTab[i];
                int actualHosts = (int) Math.pow(2, 32 - subnet.getMasque()) - 2;
                System.out.printf("%-20s | /%2d | %10s | Hôtes: %8d/%8d%n", 
                    subnet.getName(), 
                    subnet.getMasque(), 
                    subnet.getAddresseReseau(),
                    subnet.getHosts(),
                    actualHosts);
            }

        } catch (IllegalArgumentException e) {
            System.err.println("\n❌ Erreur de validation: " + e.getMessage());
            System.err.println("Veuillez vérifier vos données et réessayer.");
        } catch (RuntimeException e) {
            System.err.println("\n❌ Erreur de calcul: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
            }
        } catch (Exception e) {
            System.err.println("\n❌ Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
            System.out.println("\n" + "=".repeat(60));
            System.out.println("    Merci d'avoir utilisé le calculateur VLSM!");
            System.out.println("=".repeat(60));
        }
    }
}