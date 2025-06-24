import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class About extends JFrame {
    
    public About() {
        initializeComponents();
        setupLayout();
        setupWindow();
    }
    
    private void initializeComponents() {
        // Nothing specific to initialize for this simple about window
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Create main panel with gradient background similar to main window
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
                g2d.setColor(new Color(255, 255, 255, 15));
                for (int i = 0; i < getWidth(); i += 40) {
                    for (int j = 0; j < getHeight(); j += 40) {
                        g2d.drawOval(i, j, 80, 80);
                    }
                }
            }
        };
        
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Create content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Title
        JLabel titleLabel = new JLabel("About Subnito");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Version info
        JLabel versionLabel = new JLabel("Version 1.0.0");
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        versionLabel.setForeground(Color.WHITE);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(versionLabel);
        
        contentPanel.add(Box.createVerticalStrut(30));
        
        // Description
        String description = "<html><div style='color: white;'>" +
            "<p><b>Subnito</b> is a smart and intuitive VLSM (Variable Length Subnet Masking) " +
            "calculator built to make subnetting fast, accurate, and hassle-free.</p><br>" +
            
            "<p>With traditional subnetting, networks are often split into equal-sized blocks—" +
            "leading to wasted IP addresses. VLSM solves this by allowing different subnet sizes " +
            "within the same network, giving you greater flexibility and efficiency in IP allocation.</p><br>" +
            
            "<p><b>Subnito helps you:</b></p>" +
            "<ul style='text-align: left; margin-left: 50px;'>" +
            "<li>Calculate optimal subnets based on your IP and host requirements</li>" +
            "<li>Visualize IP ranges, subnet masks, and broadcast addresses</li>" +
            "<li>Implement VLSM for efficient network design</li>" +
            "<li>Validate IP addresses and network configurations</li>" +
            "</ul><br>" +
            
            "<p><b>Features:</b></p>" +
            "<ul style='text-align: left; margin-left: 50px;'>" +
            "<li>Intuitive and user-friendly interface</li>" +
            "<li>Real-time input validation</li>" +
            "<li>Support for CIDR notation and dotted decimal masks</li>" +
            "<li>Comprehensive subnet calculation results</li>" +
            "</ul>" +
            "</div></html>";
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(descLabel);
        
        contentPanel.add(Box.createVerticalStrut(30));
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setBackground(new Color(144, 238, 144));
        closeButton.setForeground(new Color(46, 125, 50));
        closeButton.setBorder(BorderFactory.createRaisedBevelBorder());
        closeButton.setPreferredSize(new Dimension(80, 35));
        closeButton.setFocusPainted(false);
        
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void setupWindow() {
        setTitle("About Subnito");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(true);
    }
    
    //! For testing purposes
    public static void main(String[] args) {
    // Set system look and feel
    try {
        UIManager.setLookAndFeel(UIManager.getLookAndFeel());
    } catch (Exception e) {
        System.err.println("Could not set system look and feel: " + e.getMessage());
    }
    
    SwingUtilities.invokeLater(() -> {
        new About().setVisible(true);
    });
    }
}