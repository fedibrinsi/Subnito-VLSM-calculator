import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SubnetsCreation extends JFrame {
    private String ipAddress;
    private String networkMask;
    private Subnet[] subnets;
    private JTable subnetsTable;
    private DefaultTableModel tableModel;
    private JLabel networkInfoLabel;
    
    public SubnetsCreation(String ipAddress, String networkMask, Subnet[] subnets) {
        this.ipAddress = ipAddress;
        this.networkMask = networkMask;
        this.subnets = subnets;
        
        initializeComponents();
        calculateSubnets();
        setupLayout();
        setupWindow();
    }
    
    private void initializeComponents() {
        // Create table model
        String[] columnNames = {
            "Subnet Name", "Required Hosts", "Network Address", "Subnet Mask", 
            "First Host", "Last Host", "Broadcast Address", "Available Hosts"
        };
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        subnetsTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                
                // Alternate row colors
                if (!isRowSelected(row)) {
                    if (row % 2 == 0) {
                        component.setBackground(Color.WHITE);
                    } else {
                        component.setBackground(new Color(245, 245, 245));
                    }
                } else {
                    component.setBackground(new Color(184, 207, 229));
                }
                
                return component;
            }
        };
        
        subnetsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        subnetsTable.setRowHeight(25);
        subnetsTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        // Style table header
        JTableHeader header = subnetsTable.getTableHeader();
        header.setBackground(new Color(76, 175, 80));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Network info label
        networkInfoLabel = new JLabel();
        networkInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        networkInfoLabel.setForeground(Color.WHITE);
        networkInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    private void calculateSubnets() {
    try {
        // Sort subnets by required hosts in descending order (VLSM requirement)
        java.util.Arrays.sort(subnets, (a, b) -> Integer.compare(b.getHotes(), a.getHotes()));
        
        // Parse network mask to get CIDR
        int cidr = parseMaskToCIDR(networkMask);
        
        // Calculate total available hosts in the network
        long totalAvailableHosts = (1L << (32 - cidr)) - 2;
        
        // Calculate total required hosts
        int totalRequiredHosts = 0;
        for (Subnet subnet : subnets) {
            totalRequiredHosts += subnet.getHotes();
        }
        
        // Check if we have enough space
        if (totalRequiredHosts > totalAvailableHosts) {
            JOptionPane.showMessageDialog(this, 
                "Not enough address space for the requested subnets!\n" +
                "Available hosts: " + totalAvailableHosts + "\n" +
                "Required hosts: " + totalRequiredHosts,
                "Subnet Calculation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Convert IP address to long
        String[] ipParts = ipAddress.split("\\.");
        long networkAddress = 0;
        for (int i = 0; i < 4; i++) {
            networkAddress = (networkAddress << 8) + Integer.parseInt(ipParts[i]);
        }
        
        // Apply network mask to get actual network address
        long mask = (-1L << (32 - cidr)) & 0xFFFFFFFFL;
        networkAddress = networkAddress & mask;
        
        long currentNetwork = networkAddress;
        
        // Calculate each subnet
        for (int i = 0; i < subnets.length; i++) {
            Subnet subnet = subnets[i];
            int requiredHosts = subnet.getHotes();
            
            // Calculate bits needed for hosts (hostBits) and new CIDR
            int hostBits = (int) Math.ceil(Math.log(requiredHosts + 2) / Math.log(2));
            int subnetCIDR = 32 - hostBits;
            
            // Calculate subnet size
            long subnetSize = 1L << hostBits;
            
            // Check if subnet fits in remaining space
            if ((currentNetwork & 0xFF) + subnetSize > 256) {
                // Move to next octet if current one is full
                currentNetwork = ((currentNetwork >> 8) + 1) << 8;
            }
            
            // Set subnet properties
            subnet.setAddresseReseau(longToIP(currentNetwork));
            subnet.setMasque(subnetCIDR);
            subnet.setAddresseBroadcast(longToIP(currentNetwork + subnetSize - 1));
            subnet.setPremAddUtilisable(longToIP(currentNetwork + 1));
            subnet.setDernAddUtilisable(longToIP(currentNetwork + subnetSize - 2));
            
            // Add to table
            Object[] rowData = {
                subnet.getName(),
                requiredHosts,
                subnet.getAddresseReseau(),
                cidrToMask(subnetCIDR),
                subnet.getPremAddUtilisable(),
                subnet.getDernAddUtilisable(),
                subnet.getAddresseBroadcast(),
                subnetSize - 2  // Available hosts
            };
            
            tableModel.addRow(rowData);
            
            // Move to next subnet
            currentNetwork += subnetSize;
        }
        
        // Update network info
        networkInfoLabel.setText(String.format(
            "Original Network: %s/%d | Total Available Hosts: %d | Total Required Hosts: %d",
            longToIP(networkAddress), cidr, totalAvailableHosts, totalRequiredHosts
        ));
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, 
            "Error calculating subnets: " + e.getMessage(),
            "Calculation Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
    
    private int parseMaskToCIDR(String mask) {
        try {
            // Try parsing as CIDR notation first
            return Integer.parseInt(mask);
        } catch (NumberFormatException e) {
            // Parse dotted decimal notation
            String[] parts = mask.split("\\.");
            if (parts.length != 4) {
                throw new IllegalArgumentException("Invalid subnet mask format");
            }
            
            long maskValue = 0;
            for (int i = 0; i < 4; i++) {
                maskValue = (maskValue << 8) + Integer.parseInt(parts[i]);
            }
            
            // Count consecutive 1 bits from the left
            int cidr = 0;
            for (int i = 31; i >= 0; i--) {
                if ((maskValue & (1L << i)) != 0) {
                    cidr++;
                } else {
                    break;
                }
            }
            
            return cidr;
        }
    }
    
    private String longToIP(long ip) {
        return String.format("%d.%d.%d.%d",
            (ip >> 24) & 0xFF,
            (ip >> 16) & 0xFF,
            (ip >> 8) & 0xFF,
            ip & 0xFF);
    }
    
    private String cidrToMask(int cidr) {
        long mask = (-1L << (32 - cidr)) & 0xFFFFFFFFL;
        return longToIP(mask);
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
            }
        };
        
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel("VLSM Subnet Calculation Results");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(titleLabel);
        
        titlePanel.add(Box.createVerticalStrut(10));
        titlePanel.add(networkInfoLabel);
        titlePanel.add(Box.createVerticalStrut(15));
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Table panel
        JScrollPane scrollPane = new JScrollPane(subnetsTable);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(76, 175, 80), 2));
        
        // Set column widths
        subnetsTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Subnet #
        subnetsTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Network Address
        subnetsTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Subnet Mask
        subnetsTable.getColumnModel().getColumn(3).setPreferredWidth(120); // First Host
        subnetsTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Last Host
        subnetsTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Broadcast
        subnetsTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Total Hosts
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        
        JButton exportButton = new JButton("Export to CSV");
        exportButton.setFont(new Font("Arial", Font.BOLD, 12));
        exportButton.setBackground(new Color(144, 238, 144));
        exportButton.setForeground(new Color(46, 125, 50));
        exportButton.setBorder(BorderFactory.createRaisedBevelBorder());
        exportButton.setPreferredSize(new Dimension(120, 35));
        exportButton.setFocusPainted(false);
        
        JButton newCalculationButton = new JButton("New Calculation");
        newCalculationButton.setFont(new Font("Arial", Font.BOLD, 12));
        newCalculationButton.setBackground(new Color(144, 238, 144));
        newCalculationButton.setForeground(new Color(46, 125, 50));
        newCalculationButton.setBorder(BorderFactory.createRaisedBevelBorder());
        newCalculationButton.setPreferredSize(new Dimension(130, 35));
        newCalculationButton.setFocusPainted(false);
        
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 12));
        closeButton.setBackground(new Color(144, 238, 144));
        closeButton.setForeground(new Color(46, 125, 50));
        closeButton.setBorder(BorderFactory.createRaisedBevelBorder());
        closeButton.setPreferredSize(new Dimension(80, 35));
        closeButton.setFocusPainted(false);
        
        // Button event listeners
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToCSV();
            }
        });
        
        newCalculationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    new Subnito().setVisible(true);
                });
            }
        });
        
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        buttonPanel.add(exportButton);
        buttonPanel.add(newCalculationButton);
        buttonPanel.add(closeButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Subnet Calculation Results");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
        fileChooser.setSelectedFile(new java.io.File("subnet_results.csv"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            
            // Ensure .csv extension
            if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".csv");
            }
            
            try (java.io.PrintWriter writer = new java.io.PrintWriter(fileToSave)) {
                // Write header
                StringBuilder header = new StringBuilder();
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    if (i > 0) header.append(",");
                    header.append("\"").append(tableModel.getColumnName(i)).append("\"");
                }
                writer.println(header.toString());
                
                // Write data
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    StringBuilder line = new StringBuilder();
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        if (col > 0) line.append(",");
                        Object value = tableModel.getValueAt(row, col);
                        line.append("\"").append(value != null ? value.toString() : "").append("\"");
                    }
                    writer.println(line.toString());
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Results exported successfully to:\n" + fileToSave.getAbsolutePath(),
                    "Export Successful", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (java.io.IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting results:\n" + e.getMessage(),
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void setupWindow() {
        setTitle("Subnito - Subnet Creation Results");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(800, 500));
    }
}