import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;


public class Subnito extends JFrame {
    private JTextField ipAddressField;
    private JTextField networkMaskField;
    private JTextField subnetsNumberField;
    private JButton okButton;
    private JButton infoButton;
    
    // IP address validation pattern
    private static final String IP_PATTERN = 
        "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    
    // Network mask validation pattern (CIDR notation or dotted decimal)
    private static final String MASK_PATTERN = 
        "^(([0-9]|[1-2][0-9]|3[0-2])|(((255\\.){3}(255|254|252|248|240|224|192|128|0+))|((255\\.){2}(255|254|252|248|240|224|192|128|0+)\\.0)|((255\\.)(255|254|252|248|240|224|192|128|0+)(\\.0){2})|((255|254|252|248|240|224|192|128|0+)(\\.0){3})))$";
    
    public Subnito() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setupWindow();
    }
    
    private void initializeComponents() {
        // Create input fields
        ipAddressField = new JTextField(20);
        networkMaskField = new JTextField(20);
        subnetsNumberField = new JTextField(20);
        
        // Style input fields
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);
        Color fieldBg = new Color(220, 220, 220);
        
        ipAddressField.setFont(fieldFont);
        ipAddressField.setBackground(fieldBg);
        ipAddressField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        
        networkMaskField.setFont(fieldFont);
        networkMaskField.setBackground(fieldBg);
        networkMaskField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        
        subnetsNumberField.setFont(fieldFont);
        subnetsNumberField.setBackground(fieldBg);
        subnetsNumberField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        
        // Create OK button
        okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.BOLD, 16));
        okButton.setBackground(new Color(144, 238, 144));
        okButton.setForeground(new Color(46, 125, 50));
        okButton.setBorder(BorderFactory.createRaisedBevelBorder());
        okButton.setPreferredSize(new Dimension(100, 40));
        okButton.setFocusPainted(false);
        
        // Create info button (circular)
        infoButton = new JButton("i");
        infoButton.setFont(new Font("Arial", Font.BOLD, 18));
        infoButton.setBackground(Color.WHITE);
        infoButton.setForeground(new Color(76, 175, 80));
        infoButton.setPreferredSize(new Dimension(40, 40));
        infoButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        infoButton.setFocusPainted(false);
        
        // Make info button circular
        infoButton.setContentAreaFilled(false);
        infoButton.setOpaque(true);
        infoButton.setBorderPainted(true);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Create main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Create gradient from light green to darker green
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(129, 199, 132),
                    0, getHeight(), new Color(76, 175, 80)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Add subtle pattern overlay
                g2d.setColor(new Color(255, 255, 255, 20));
                for (int i = 0; i < getWidth(); i += 50) {
                    for (int j = 0; j < getHeight(); j += 50) {
                        g2d.drawOval(i, j, 100, 100);
                    }
                }
            }
        };
        
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Title
        JLabel titleLabel = new JLabel("Welcome to Subnito");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(40, 0, 40, 0);
        mainPanel.add(titleLabel, gbc);
        
        // IP Address
        JLabel ipLabel = new JLabel("IP address");
        ipLabel.setFont(new Font("Arial", Font.BOLD, 16));
        ipLabel.setForeground(Color.WHITE);
        
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 40, 5, 0);
        mainPanel.add(ipLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 40, 20, 40);
        mainPanel.add(ipAddressField, gbc);
        
        // Network Mask
        JLabel maskLabel = new JLabel("Network mask");
        maskLabel.setFont(new Font("Arial", Font.BOLD, 16));
        maskLabel.setForeground(Color.WHITE);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 40, 5, 0);
        mainPanel.add(maskLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 40, 20, 40);
        mainPanel.add(networkMaskField, gbc);
        
        // Subnets Number
        JLabel subnetsLabel = new JLabel("Subnets number");
        subnetsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        subnetsLabel.setForeground(Color.WHITE);
        
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 40, 5, 0);
        mainPanel.add(subnetsLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 40, 30, 40);
        mainPanel.add(subnetsNumberField, gbc);
        
        // OK Button
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 40, 0);
        mainPanel.add(okButton, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Info button in top right
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(infoButton, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(topPanel, BorderLayout.NORTH);
    }
    
    private void setupEventListeners() {
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateInputs()) {
                    openSubnetsCreation();
                }
            }
        });
        
        infoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAbout();
            }
        });
    }
    
    private boolean validateInputs() {
        String ipAddress = ipAddressField.getText().trim();
        String networkMask = networkMaskField.getText().trim();
        String subnetsNumber = subnetsNumberField.getText().trim();
        
        // Validate IP Address
        if (ipAddress.isEmpty()) {
            showError("IP address cannot be empty!");
            ipAddressField.requestFocus();
            return false;
        }
        
        if (!Pattern.matches(IP_PATTERN, ipAddress)) {
            showError("Invalid IP address format!\nPlease enter a valid IP address (e.g., 192.168.1.1)");
            ipAddressField.requestFocus();
            return false;
        }
        
        // Validate Network Mask
        if (networkMask.isEmpty()) {
            showError("Network mask cannot be empty!");
            networkMaskField.requestFocus();
            return false;
        }
        
        if (!isValidNetworkMask(networkMask)) {
            showError("Invalid network mask format!\nPlease enter CIDR notation (e.g., 24) or dotted decimal (e.g., 255.255.255.0)");
            networkMaskField.requestFocus();
            return false;
        }
        
        // Validate Subnets Number
        if (subnetsNumber.isEmpty()) {
            showError("Subnets number cannot be empty!");
            subnetsNumberField.requestFocus();
            return false;
        }
        
        try {
            int subnets = Integer.parseInt(subnetsNumber);
            if (subnets <= 0 || subnets > 65536) {
                showError("Subnets number must be between 1 and 65536!");
                subnetsNumberField.requestFocus();
                return false;
            }
        } catch (NumberFormatException ex) {
            showError("Subnets number must be a valid integer!");
            subnetsNumberField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private boolean isValidNetworkMask(String mask) {
        // Check if it's CIDR notation (0-32)
        try {
            int cidr = Integer.parseInt(mask);
            return cidr >= 0 && cidr <= 32;
        } catch (NumberFormatException e) {
            // Not CIDR, check if it's dotted decimal
            return Pattern.matches(MASK_PATTERN, mask);
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    private void openSubnetsCreation() {
        // Create and show SubnetsInput frame
        SwingUtilities.invokeLater(() -> {
            try {
                dispose(); // Close main window
                SubnetsInput subnetsInputFrame = new SubnetsInput(
                    ipAddressField.getText().trim(),
                    networkMaskField.getText().trim(),
                    Integer.parseInt(subnetsNumberField.getText().trim())
                );
                subnetsInputFrame.setVisible(true);
            } catch (Exception e) {
                showError("Error opening Subnets Input window: " + e.getMessage());
            }
        });
    }
    
    private void openAbout() {
        // Create and show About frame
        SwingUtilities.invokeLater(() -> {
            try {
                About aboutFrame = new About();
                aboutFrame.setVisible(true);
            } catch (Exception e) {
                showError("Error opening About window: " + e.getMessage());
            }
        });
    }
    
    private void setupWindow() {
        setTitle("Subnito - VLSM Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }
        
        SwingUtilities.invokeLater(() -> {
            new Subnito().setVisible(true);
        });
    }
}
