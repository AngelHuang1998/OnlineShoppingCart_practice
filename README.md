一個簡單的「線上購物車結帳系統」練習專案，重點在 封裝 + 介面多型（折扣模式）：購物車只負責計算總價與套用折扣，不需要知道每種折扣怎麼算。

A. Features:

    1. 商品 Product：品名 + 價錢，並在建構時做基本資料驗證（品名不可空、價錢不可負）

    2. 購物車項目 CartItem：一個商品 + 數量，並可計算小計 price * quantity

    3. 折扣策略介面 DiscountStrategy：applyDiscount(total)

    4. 三種折扣策略：

        4-1. NoDiscount：不折扣      
        4-2. PercentageDiscount：打幾折（例如 0.9 代表 9 折）
        4-3. ThresholdDiscount：滿額折扣（若折扣超過總價則歸 0）

    5. ShoppingCart：

        5-1. addItemtoCart(CartItem item): 將商品加到購物車
        
        5-2. totalBeforeDiscount()：折扣前總金額
        
        5-3. setDiscountStrategy(DiscountStrategy s)：可隨時更換折扣策略，若傳入 null 會自動使用 NoDiscount
        
        5-4. totalAfterDiscount()：折扣後總金額（未設定策略時預設 NoDiscount）


B. Project Structure (Single File Demo)

此專案以單一檔案示範（OnlineShoppingDemo.java），內容包含：

    1. OnlineShoppingDemo（main demo）

    2. 自訂例外：InvalidPrice, InvalidAmount, InvalidArgument

    3. 核心類別：Product, CartItem

    4. 策略介面與實作：DiscountStrategy, NoDiscount, PercentageDiscount, ThresholdDiscount

    5. ShoppingCart
    
