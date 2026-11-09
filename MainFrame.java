import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {
    private Inventory inventory;
    private JTextField txtId, txtName, txtQty, txtPrice, txtSearch;
    private JComboBox<String> comboCategory, comboSupplier;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTotalValue, lblCount, lblLowStock;

    public MainFrame() {
        inventory = new Inventory();
        setTitle("Inventory Management System - Group Project");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        loadSampleData();
        refreshTable(inventory.getAllProducts());
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(44, 62, 80));
        JLabel title = new JLabel("INVENTORY MANAGEMENT SYSTEM");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        topPanel.add(title);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Category", "Qty", "Price (UGX)", "Total Value", "Supplier", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Product Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Product ID:"), gbc);
        gbc.gridx = 1; txtId = new JTextField(12); formPanel.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; txtName = new JTextField(12); formPanel.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        String[] cats = {"Electronics", "Groceries", "Stationery", "Clothing", "Hardware", "Other"};
        comboCategory = new JComboBox<>(cats);
        formPanel.add(comboCategory, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; txtQty = new JTextField(12); formPanel.add(txtQty, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1; txtPrice = new JTextField(12); formPanel.add(txtPrice, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Supplier:"), gbc);
        gbc.gridx = 1;
        comboSupplier = new JComboBox<>();
        updateSupplierCombo();
        formPanel.add(comboSupplier, gbc);

        JPanel btnPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        JButton btnAdd = new JButton("Add Product");
        JButton btnUpdate = new JButton("Update Qty");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear Form");
        JButton btnSearch = new JButton("Search");
        JButton btnShowAll = new JButton("Show All");
        btnPanel.add(btnAdd); btnPanel.add(btnUpdate); btnPanel.add(btnDelete);
        btnPanel.add(btnClear); btnPanel.add(btnSearch); btnPanel.add(btnShowAll);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);
        gbc.gridy = 7;
        formPanel.add(new JLabel("Search keyword:"), gbc);
        gbc.gridy = 8;
        txtSearch = new JTextField(12);
        formPanel.add(txtSearch, gbc);
        add(formPanel, BorderLayout.WEST);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblCount = new JLabel("Total Products: 0");
        lblTotalValue = new JLabel(" | Total Value: 0 UGX");
        lblLowStock = new JLabel(" | Low Stock Items: 0");
        lblLowStock.setForeground(Color.RED);
        bottomPanel.add(lblCount); bottomPanel.add(lblTotalValue); bottomPanel.add(lblLowStock);
        add(bottomPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addProductAction());
        btnUpdate.addActionListener(e -> updateQuantityAction());
        btnDelete.addActionListener(e -> deleteProductAction());
        btnClear.addActionListener(e -> clearForm());
        btnSearch.addActionListener(e -> searchAction());
        btnShowAll.addActionListener(e -> refreshTable(inventory.getAllProducts()));

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtId.setText(tableModel.getValueAt(row, 0).toString());
                    txtName.setText(tableModel.getValueAt(row, 1).toString());
                    comboCategory.setSelectedItem(tableModel.getValueAt(row, 2).toString());
                    txtQty.setText(tableModel.getValueAt(row, 3).toString());
                    txtPrice.setText(tableModel.getValueAt(row, 4).toString());
                    comboSupplier.setSelectedItem(tableModel.getValueAt(row, 6).toString());
                }
            }
        });
    }

    private void addProductAction() {
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String category = (String) comboCategory.getSelectedItem();
        String qtyStr = txtQty.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String supplier = (String) comboSupplier.getSelectedItem();

        if (id.isEmpty() || name.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty() || supplier == null) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int qty;
        double price;
        try {
            qty = Integer.parseInt(qtyStr);
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity and Price must be numbers!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (qty < 0 || price < 0) {
            JOptionPane.showMessageDialog(this, "Quantity and price cannot be negative!");
            return;
        }

        String msg = "";
        switch (category) {
            case "Electronics": msg = " - Handle with care!"; break;
            case "Groceries": msg = " - Check expiry date!"; break;
            case "Stationery": msg = " - Store in dry place!"; break;
            default: msg = "";
        }

        Product p = new Product(id, name, category, qty, price, supplier);
        if (inventory.addProduct(p)) {
            JOptionPane.showMessageDialog(this, "Product added successfully!" + msg);
            refreshTable(inventory.getAllProducts());
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed! Duplicate ID or storage full.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateQuantityAction() {
        String id = txtId.getText().trim();
        String qtyStr = txtQty.getText().trim();
        if (id.isEmpty() || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a product and enter new quantity!");
            return;
        }
        try {
            int newQty = Integer.parseInt(qtyStr);
            if (inventory.updateQuantity(id, newQty)) {
                JOptionPane.showMessageDialog(this, "Quantity updated!");
                refreshTable(inventory.getAllProducts());
            } else {
                JOptionPane.showMessageDialog(this, "Product not found or quantity is negative!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity!");
        }
    }

    private void deleteProductAction() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a product to delete!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete product " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (inventory.deleteProduct(id)) {
                JOptionPane.showMessageDialog(this, "Deleted!");
                refreshTable(inventory.getAllProducts());
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Not found!");
            }
        }
    }

    private void searchAction() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            refreshTable(inventory.getAllProducts());
        } else {
            Product[] results = inventory.searchProducts(keyword);
            refreshTable(results);
            if (results.length == 0) JOptionPane.showMessageDialog(this, "No products found for: " + keyword);
        }
    }

    private void refreshTable(Product[] list) {
        tableModel.setRowCount(0);
        for (int i = 0; i < list.length; i++) {
            Product p = list[i];
            String status = p.isLowStock() ? "LOW STOCK" : "In Stock";
            Object[] row = {p.getId(), p.getName(), p.getCategory(), p.getQuantity(), p.getPrice(),
                    p.getTotalValue(), p.getSupplierId(), status};
            tableModel.addRow(row);
        }
        updateStats();
    }

    private void updateStats() {
        lblCount.setText("Total Products: " + inventory.getProductCount());
        lblTotalValue.setText(" | Total Value: " + String.format("%.2f UGX", inventory.getTotalInventoryValue()));
        lblLowStock.setText(" | Low Stock Items: " + inventory.getLowStockProducts().length);
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtQty.setText("");
        txtPrice.setText("");
        txtSearch.setText("");
        comboCategory.setSelectedIndex(0);
        if (comboSupplier.getItemCount() > 0) comboSupplier.setSelectedIndex(0);
        table.clearSelection();
    }

    private void updateSupplierCombo() {
        comboSupplier.removeAllItems();
        for (Supplier s : inventory.getAllSuppliers()) comboSupplier.addItem(s.getSupplierId());
    }

    private void loadSampleData() {
        inventory.addProduct(new Product("P001", "Laptop Dell", "Electronics", 5, 2500000, "SUP002"));
        inventory.addProduct(new Product("P002", "A4 Books", "Stationery", 100, 5000, "SUP003"));
        inventory.addProduct(new Product("P003", "Maize Flour", "Groceries", 8, 3000, "SUP001"));
    }
}
