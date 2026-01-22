package HaHow.practice;

import java.util.ArrayList;
import java.util.List;
/*
<線上購物車結帳系統說明>
商品: 有 品名、價錢
購物車: 有 商品、商品數量
三種折扣方案可以套用: NoDiscount(無折扣)、percentageDiscount(打幾折)、thresholdDiscount(滿多少折多少)

可以對購物車進行以下幾種操作:
1. 把商品加入購物車
2. 查詢折扣"前"，購物車的總金額
3. 指定套用哪種折扣方案
4. 根據套用的折扣方案，查詢折扣"後"，購物車的總金額
*/

public class OnlineShoppingDemo {
    public static void main(String[] args) {

        /*A. 先測 Product 失敗案例：要用 try/catch 才能繼續往下跑 */
        // 案例1: 名字是空字串
        try {
            Product snack  = new Product(" ", 60);
        } catch (Exception e) {
            System.out.println("snack 建立失敗（符合預期）: " + e.getMessage());
        }
        // 案例2: 價錢 < 0
        try {
            Product beer  = new Product("Beer", -100);
        } catch (Exception e) {
            System.out.println("beer 建立失敗（符合預期）: " + e.getMessage());
        }

        
        /*B. 正常流程：建立商品 + 加入購物車 + 算折扣*/
        try {
            Product coffee = new Product("Coffee", 150);
            Product bread  = new Product("Bread", 80);

            ShoppingCart cart = new ShoppingCart();
            cart.addItemtoCart(new CartItem(coffee, 3)); // 450
            cart.addItemtoCart(new CartItem(bread, 5));  // 400

            System.out.println("Original total = " + cart.totalBeforeDiscount());
            System.out.println("");
            System.out.println("No strategy set (default NoDiscount) = " + cart.totalAfterDiscount());
            System.out.println("");

            cart.setDiscountStrategy(new PercentageDiscount(0.9));
            System.out.println("9折後 = " + cart.totalAfterDiscount());
            System.out.println("");

            cart.setDiscountStrategy(new NoDiscount());
            System.out.println("沒折扣後 = " + cart.totalAfterDiscount());
            System.out.println("");

            cart.setDiscountStrategy(new ThresholdDiscount(800, 100));
            System.out.println("滿800折100後 = " + cart.totalAfterDiscount());
            System.out.println("");
            cart.setDiscountStrategy(new ThresholdDiscount(1000, 100));
            System.out.println("滿1000折100後 = " + cart.totalAfterDiscount());  // 因未達折扣門檻，故維持原價

        } catch (Exception e) {
            System.out.println("正常流程不該失敗，但發生錯誤: " + e.getMessage());
        }
    }
}



// 自訂例外InvalidPrice
class InvalidPrice extends Exception{
    // 建構子
    public InvalidPrice(String message){
        super(message);
    }
}
// 自訂例外InvalidAmount
class InvalidAmount extends Exception{
    // 建構子
    public InvalidAmount(String message){
        super(message);
    }
}
// 自訂例外InvalidArgument (參數異常)
class InvalidArgument extends Exception{
    // 建構子
    public InvalidArgument(String message){
        super(message);
    }
}


// Product
class Product{
    private String name;
    private double price;

    // getter
    public String getName(){
        return name;
    }
    // getter
    public double getPrice(){
        return price;
    }

    // 建構子
    public Product(String name, double price) throws InvalidPrice, InvalidArgument {
        if(name == null || name.trim().isEmpty()){  // 把null跟空字串都擋掉
            throw new InvalidArgument("產品名稱不能是空的");
        }
        if(price < 0){
            throw new InvalidPrice("產品價格需>=0");
        }
        this.name = name;
        this.price = price;
    }
}

// CartItem
class CartItem{
    private Product product;
    private int quantity;
    // getter
    public Product getProduct(){
        return product;
    }
    // getter
    public int getQuantity(){
        return quantity;
    }

    // 建構子
    public CartItem(Product product, int quantity) throws InvalidArgument, InvalidAmount {
        if (product == null) {
            throw new InvalidArgument("Product 不能是 null");
        }
        if (quantity <= 0) {
            throw new InvalidAmount("數量需 > 0");
        }
        this.product = product;
        this.quantity = quantity;
    }

    public double getSubtotal(){
        return product.getPrice() * quantity;
    }
}


// DiscountStrategy介面
interface DiscountStrategy {
    public double applyDiscount(double total);
}
// 實作DiscountStrategy介面(1. NoDiscount  2. percentageDiscount  3. ThresholdDiscount)
// 1. NoDiscount
class NoDiscount implements DiscountStrategy{
    @Override
    public double applyDiscount(double total){
        return total;
    }
}
// 2. percentageDiscount (打幾折)
class PercentageDiscount implements DiscountStrategy{
    private double rate; // 0~1
    // 建構子
    public PercentageDiscount(double rate) throws InvalidArgument {
        if(rate < 0 || rate > 1){
            throw new InvalidArgument("折扣需介於0~1之間");
        }
        this.rate = rate;
    }
    // getter
    public double getRate(){
        return rate;
    }
    @Override
    public double applyDiscount(double total){
        return total * rate;
    }
}
// 3. ThresholdDiscount (滿多少(threshold)，折價多少(discount))
class ThresholdDiscount implements DiscountStrategy{
    private double threshold;
    private double discount;
    // 建構子
    public ThresholdDiscount(double threshold, double discount) throws InvalidArgument{
        if(threshold<0){
            throw new InvalidArgument("折扣門檻需 >= 0");
        }
        if(discount <= 0){
            throw new InvalidArgument("折扣需 > 0");
        }
        this.threshold = threshold;
        this.discount = discount;
    }
    // getter
    public double getThreshold(){
        return threshold;
    }
    // getter
    public double getDiscount(){
        return discount;
    }
    @Override
    public double applyDiscount(double total){
        double newTotal;
        if(total >= threshold){   // 達到折扣門檻
            if(total <= discount){  // 當折扣比原價多
                newTotal = 0;
            }else{  // 當折扣比原價少
                newTotal = total - discount;
            }
        }else{  // 未達到折扣門檻
            return total;
        }
        return newTotal;
    }
}



// ShoppingCart 各式操作
class ShoppingCart {
    private List<CartItem> cart = new ArrayList<CartItem>();
    private DiscountStrategy strategy;  //變數型別是"介面"（DiscountStrategy）
    //getter
    public DiscountStrategy getDiscountStrategy(){
        return strategy;
    }


    // 把商品加入購物車
    public void addItemtoCart(CartItem item) throws InvalidAmount{
        if(item.getQuantity() <= 0){
            throw new InvalidAmount("加入購物車的商品數量需 > 0");
        }
        cart.add(item);
    }

    // 查詢折扣"前"總金額
    public double totalBeforeDiscount(){
        double totalBeforeDiscount = 0;
        for(CartItem item: cart){
            totalBeforeDiscount += item.getSubtotal();
        }
        return totalBeforeDiscount;
    }

    // 指定一種折扣策略（可隨時更換）
    /*
    變數型別是介面（DiscountStrategy）
    實際放進去的物件可以是任何實作它的類別（NoDiscount / PercentageDiscount / ThresholdDiscount）
    */
    public void setDiscountStrategy(DiscountStrategy s){
        if(s == null){
            this.strategy = new NoDiscount();
        }else{
            this.strategy = s;
        }
    }

    // 查詢折扣"後"總金額
    public double totalAfterDiscount(){
        if(strategy == null){
            this.strategy = new NoDiscount();
        }
        double totalAfterDiscount = strategy.applyDiscount(totalBeforeDiscount());
        return totalAfterDiscount;
    }
    
} 