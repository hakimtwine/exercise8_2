public class Inventory {
    private Product[] products;
    private int productCount;
    private Supplier[] suppliers;
    private int supplierCount;
    private static final int MAX_SIZE = 100;

    public Inventory() {
        products = new Product[MAX_SIZE];
        suppliers = new Supplier[MAX_SIZE];
        productCount = 0;
        supplierCount = 0;
        addSupplier(new Supplier("SUP001", "General Supplies Ltd", "0700-123456"));
        addSupplier(new Supplier("SUP002", "Tech Distributors", "0700-654321"));
        addSupplier(new Supplier("SUP003", "Office World", "0700-112233"));
    }

    public boolean addSupplier(Supplier s) {
        if (s == null || supplierCount >= MAX_SIZE) return false;
        for (int i = 0; i < supplierCount; i++) {
            if (suppliers[i].getSupplierId().equalsIgnoreCase(s.getSupplierId())) return false;
        }
        suppliers[supplierCount++] = s;
        return true;
    }

    public Supplier[] getAllSuppliers() {
        Supplier[] result = new Supplier[supplierCount];
        for (int i = 0; i < supplierCount; i++) result[i] = suppliers[i];
        return result;
    }

    public boolean addProduct(Product p) {
        if (p == null || productCount >= MAX_SIZE) return false;
        if (p.getId() == null || p.getId().trim().isEmpty()) return false;
        for (int i = 0; i < productCount; i++) {
            if (products[i].getId().equalsIgnoreCase(p.getId())) return false;
        }
        if (p.getQuantity() < 0 || p.getPrice() < 0) return false;
        products[productCount++] = p;
        return true;
    }

    public boolean deleteProduct(String id) {
        if (id == null) return false;
        for (int i = 0; i < productCount; i++) {
            if (products[i].getId().equalsIgnoreCase(id)) {
                for (int j = i; j < productCount - 1; j++) products[j] = products[j + 1];
                products[--productCount] = null;
                return true;
            }
        }
        return false;
    }

    public boolean updateQuantity(String id, int newQty) {
        if (newQty < 0) return false;
        Product p = findProductById(id);
        if (p != null) {
            p.setQuantity(newQty);
            return true;
        }
        return false;
    }

    public Product findProductById(String id) {
        if (id == null) return null;
        for (int i = 0; i < productCount; i++) {
            if (products[i].getId().equalsIgnoreCase(id)) return products[i];
        }
        return null;
    }

    public Product[] searchProducts(String keyword) {
        if (keyword == null) keyword = "";
        keyword = keyword.toLowerCase();
        Product[] temp = new Product[productCount];
        int count = 0;
        for (int i = 0; i < productCount; i++) {
            if (products[i].getName().toLowerCase().contains(keyword)
                    || products[i].getCategory().toLowerCase().contains(keyword)
                    || products[i].getId().toLowerCase().contains(keyword)) {
                temp[count++] = products[i];
            }
        }
        Product[] result = new Product[count];
        for (int i = 0; i < count; i++) result[i] = temp[i];
        return result;
    }

    public Product[] getAllProducts() {
        Product[] result = new Product[productCount];
        for (int i = 0; i < productCount; i++) result[i] = products[i];
        return result;
    }

    public Product[] getLowStockProducts() {
        int count = 0;
        for (int i = 0; i < productCount; i++) if (products[i].isLowStock()) count++;
        Product[] result = new Product[count];
        int idx = 0;
        for (int i = 0; i < productCount; i++) {
            if (products[i].isLowStock()) result[idx++] = products[i];
        }
        return result;
    }

    public double getTotalInventoryValue() {
        double total = 0;
        int i = 0;
        while (i < productCount) {
            total += products[i].getTotalValue();
            i++;
        }
        return total;
    }

    public int getProductCount() { return productCount; }
}
