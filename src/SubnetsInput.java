import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubnetsInput extends JFrame {
    private String ipAddress;
    private String networkMask;
    private int subnetsNumber;
    private List<JTextField> subnetNameFields;
    private List<JTextField> hostsNumberFields;
    private JButton okButton;
    private JButton homeButton;
    private JButton infoButton;
    private JPanel inputPanel;
    
    public SubnetsInput(String ipAddress, String networkMask, int subnetsNumber) {
        this.ipAddress = ipAddress;
        this.networkMask = networkMask;
        this.subnetsNumber = subnetsNumber;
        this.subnetNameFields = new ArrayList<>();
        this.hostsNumberFields = new ArrayList<>();
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setupWindow();
    }
    
    private void initializeComponents() {
        // Create text field lists for dynamic input fields
        for (int i = 0; i < subnetsNumber; i++) {
            JTextField nameField = new JTextField(20);
            JTextField hostsField = new JTextField(20);
            
            // Style input fields
            Font fieldFont = new Font("Arial", Font.PLAIN, 14);
            Color fieldBg = new Color(220, 220, 220);
            
            nameField.setFont(fieldFont);
            nameField.setBackground(fieldBg);
            nameField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            nameField.setToolTipText("Enter a unique name for subnet " + (i + 1));
            
            hostsField.setFont(fieldFont);
            hostsField.setBackground(fieldBg);
            hostsField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            hostsField.setToolTipText("Enter number of hosts needed for subnet " + (i + 1));
            
            subnetNameFields.add(nameField);
            hostsNumberFields.add(hostsField);
        }
        
        // Create OK button
        okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.BOLD, 16));
        okButton.setBackground(new Color(144, 238, 144));
        okButton.setForeground(new Color(46, 125, 50));
        okButton.setBorder(BorderFactory.createRaisedBevelBorder());
        okButton.setPreferredSize(new Dimension(100, 40));
        okButton.setFocusPainted(false);
        
        // Create home button
        homeButton = new JButton();
        homeButton.setPreferredSize(new Dimension(40, 40));
        homeButton.setBackground(Color.WHITE);
        homeButton.setForeground(new Color(76, 175, 80));
        homeButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        homeButton.setFocusPainted(false);
        homeButton.setContentAreaFilled(false);
        homeButton.setOpaque(true);
        homeButton.setToolTipText("Return to main window");
        
        // Draw home icon
        homeButton.addActionListener(e -> {
            // Action will be added in setupEventListeners
        });
        
        // Create info button
        infoButton = new JButton("i");
        infoButton.setFont(new Font("Arial", Font.BOLD, 18));
        infoButton.setBackground(Color.WHITE);
        infoButton.setForeground(new Color(76, 175, 80));
        infoButton.setPreferredSize(new Dimension(40, 40));
        infoButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        infoButton.setFocusPainted(false);
        infoButton.setContentAreaFilled(false);
        infoButton.setOpaque(true);
        infoButton.setToolTipText("About Subnito");
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
        
        mainPanel.setLayout(new BorderLayout());
        
        // Top panel with home and info buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Custom home button with house icon
        JPanel homePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        homePanel.setOpaque(false);

        ImageIcon logoIcon = new ImageIcon("SubnitoVLSM\\src\\images\\icons8-home-50.png");
        
        JButton customHomeButton = new JButton(logoIcon) ;

        
        customHomeButton.setPreferredSize(new Dimension(40, 40));
        customHomeButton.setBackground(Color.WHITE);
        customHomeButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        customHomeButton.setFocusPainted(false);
        customHomeButton.setContentAreaFilled(true);
        customHomeButton.setOpaque(true);
        customHomeButton.setToolTipText("Return to main window");
        
        homePanel.add(customHomeButton);
        topPanel.add(homePanel, BorderLayout.WEST);
        topPanel.add(infoButton, BorderLayout.EAST);
        
        // Set the home button reference for event handling
        homeButton = customHomeButton;
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Title and subtitle panel
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("\"Slice your network. Maximize your IPs. Subnet smart with ease.\"");
        titleLabel.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(titleLabel);
        
        titlePanel.add(Box.createVerticalStrut(30));
        
        // Input section
        inputPanel = new JPanel();
        inputPanel.setOpaque(false);
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Column headers
        JLabel nameHeaderLabel = new JLabel("Subnet name");
        nameHeaderLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameHeaderLabel.setForeground(Color.WHITE);
        nameHeaderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel hostsHeaderLabel = new JLabel("Hosts number");
        hostsHeaderLabel.setFont(new Font("Arial", Font.BOLD, 16));
        hostsHeaderLabel.setForeground(Color.WHITE);
        hostsHeaderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(0, 40, 15, 20);
        gbc.anchor = GridBagConstraints.CENTER;
        inputPanel.add(nameHeaderLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.insets = new Insets(0, 20, 15, 40);
        inputPanel.add(hostsHeaderLabel, gbc);
        
        // Add input fields for each subnet
        for (int i = 0; i < subnetsNumber; i++) {
            gbc.gridx = 0; gbc.gridy = i + 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 40, 5, 20);
            inputPanel.add(subnetNameFields.get(i), gbc);
            
            gbc.gridx = 1; gbc.gridy = i + 1;
            gbc.insets = new Insets(5, 20, 5, 40);
            inputPanel.add(hostsNumberFields.get(i), gbc);
        }
        
        // Create scrollable panel for input fields
        JScrollPane scrollPane = new JScrollPane(inputPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        titlePanel.add(scrollPane);
        titlePanel.add(Box.createVerticalStrut(30));
        
        // OK Button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);
        titlePanel.add(buttonPanel);
        
        mainPanel.add(titlePanel, BorderLayout.CENTER);
        add(mainPanel);
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
        
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    new Subnito().setVisible(true);
                });
            }
        });
        
        infoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    new About().setVisible(true);
                });
            }
        });
    }
    
    private boolean validateInputs() {
        Set<String> usedNames = new HashSet<>();
        List<String> errors = new ArrayList<>();
        
        for (int i = 0; i < subnetsNumber; i++) {
            String name = subnetNameFields.get(i).getText().trim();
            String hostsText = hostsNumberFields.get(i).getText().trim();
            
            // Validate subnet name
            if (name.isEmpty()) {
                errors.add("Subnet " + (i + 1) + ": Name cannot be empty!");
                subnetNameFields.get(i).requestFocus();
            } else if (usedNames.contains(name.toLowerCase())) {
                errors.add("Subnet " + (i + 1) + ": Name '" + name + "' is already used!");
                subnetNameFields.get(i).requestFocus();
            } else {
                usedNames.add(name.toLowerCase());
            }
            
            // Validate hosts number
            if (hostsText.isEmpty()) {
                errors.add("Subnet " + (i + 1) + ": Hosts number cannot be empty!");
                if (errors.size() == 1) hostsNumberFields.get(i).requestFocus();
            } else {
                try {
                    int hosts = Integer.parseInt(hostsText);
                    if (hosts <= 0) {
                        errors.add("Subnet " + (i + 1) + ": Hosts number must be greater than 0!");
                        if (errors.size() == 1) hostsNumberFields.get(i).requestFocus();
                    } else if (hosts > 16777214) { // Maximum hosts in a /8 network - 2
                        errors.add("Subnet " + (i + 1) + ": Hosts number too large (max: 16,777,214)!");
                        if (errors.size() == 1) hostsNumberFields.get(i).requestFocus();
                    }
                } catch (NumberFormatException ex) {
                    errors.add("Subnet " + (i + 1) + ": Hosts number must be a valid integer!");
                    if (errors.size() == 1) hostsNumberFields.get(i).requestFocus();
                }
            }
        }
        
        if (!errors.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Please fix the following errors:\n\n");
            for (String error : errors) {
                errorMessage.append("• ").append(error).append("\n");
            }
            
            JOptionPane.showMessageDialog(this, errorMessage.toString(), 
                                        "Input Validation Errors", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void openSubnetsCreation() {
        // Create array of Subnet objects
        Subnet[] subnets = new Subnet[subnetsNumber];
        
        for (int i = 0; i < subnetsNumber; i++) {
            String name = subnetNameFields.get(i).getText().trim();
            int hosts = Integer.parseInt(hostsNumberFields.get(i).getText().trim());
            subnets[i] = new Subnet(name, hosts);
        }
        
        // Close current window and open SubnetsCreation
        dispose();
        SwingUtilities.invokeLater(() -> {
            try {
                SubnetsCreation subnetsFrame = new SubnetsCreation(ipAddress, networkMask, subnets);
                subnetsFrame.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, 
                    "Error opening Subnets Creation window: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void setupWindow() {
        setTitle("Subnito - Subnet Configuration");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Calculate window height based on number of subnets
        int baseHeight = 400;
        int fieldHeight = 45;
        int calculatedHeight = baseHeight + (subnetsNumber * fieldHeight);
        int maxHeight = 800;
        int finalHeight = Math.min(calculatedHeight, maxHeight);
        
        setSize(800, finalHeight);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(600, 400));
    }

        public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }
        
        SwingUtilities.invokeLater(() -> {
            new SubnetsInput("192.168.1.1", "24", 3).setVisible(true);
        });
    }
}
