public class Item {

    protected String itemName;
    protected String companyName;
    protected String companyAddress;
    protected float itemCost;
    protected float itemDiscount;
    protected float itemNewCost;
    protected int quantity;
    protected String endTime;

    public Item(String itemName, float itemCost, float itemDiscount, String companyName, String companyAddress, int quantity, String endTime){
        this.itemName = itemName;
        this.itemDiscount = itemDiscount;
        this.itemNewCost = itemCost - (itemCost * itemDiscount/100);
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.itemCost = itemCost;
        this.quantity = quantity;
        this.endTime = endTime;
    }

    public void purchase(int quantity){
        this.quantity -= quantity;
    }

    public String getCompanyName(){
        return companyName;
    }

    public float getDiscount(){
        return itemDiscount;
    }

    public String getItemName(){
        return itemName;
    }

    public float getItemNewCost(){
        return itemNewCost;
    }

    public int getQuantity(){
        return quantity;
    }

    @Override
    public String toString(){
       return "\nCompany Name : " + companyName
               + "\nCompany Address : " + companyAddress
               + "\nItem Name : " + itemName
               + "\nItem Original Cost : " + itemCost
               + "\nDiscount(%) : " + itemDiscount
               + "\nItem Current Cost : " + itemNewCost
               + "\nQuantity : " + quantity
               + "\nendTime : " + endTime
               + "\n";
    }
}
