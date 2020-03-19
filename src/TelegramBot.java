import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TelegramBot extends TelegramLongPollingBot {

    public ArrayList<Item> itemList = new ArrayList<>();
    public ArrayList<String> list = new ArrayList<>();
    private String reply = "?";
    protected String companyName = "BB";
    protected Boolean setCompanyName = false;
    protected Boolean setNewItem = false;
    protected Boolean purchaseMode = false;
    protected int createNewItem = 0;
    protected int userType = -1; // 1 - Vendor , 2 - Customer
    protected String ItemName = "";
    protected String ItemCostText = "";
    protected int ItemCost = 0;
    protected int ItemQuantity = 0;
    protected int purchaseType = 0;
    protected int discount = 0;
    protected int ItemNewCost = 0;
    protected int itemIndex = 0;
    protected int deliveryMode = 0;
    protected String ItemQuantityText = "";
    protected String endTime = "";
    protected String companyAddress = "ABC street";

    @Override
    public void onUpdateReceived(Update update) {

        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();

            SendMessage message = new SendMessage(); // Create a message object object
            message.setChatId(chat_id);
            reply = "##NEW REPLY NEEDED##";
                try{
                if (userType == -1) {
                    reply = "Welcome! Are you a /vendor or /customer?";
                    userType = 0;
                } else if (setCompanyName == true) {
                    companyName = message_text;
                    reply = "company name set to " + message_text;
                    reply += "\nHelp Menu\n"
                            + "/sortByDiscount\n"
                            + "/sortByPrice\n"
                            + "/gen\n"
                            + "/addItem\n"
                            + "/list\n";
                    setCompanyName = false;
                } else if (setNewItem == true) {
                    switch (createNewItem) {
                        case 1:
                            ItemName = message_text;
                            reply = "How much does it cost?";
                            createNewItem += 1;
                            break;
                        case 2:
                            ItemCostText = message_text;
                            reply = "How many are you selling?";
                            createNewItem += 1;
                            break;
                        case 3:
                            ItemQuantityText = message_text;
                            reply = "Discount in %?";
                            createNewItem += 1;
                            break;
                        case 4:
                            discount = Integer.parseInt(message_text);
                            reply = "endTime?";
                            createNewItem += 1;
                            break;
                        case 5:
                            endTime = message_text;
                            reply = "Item is added:\nItemName : " + ItemName + ",\nCost : " + ItemCostText + "\nQuantity : " + ItemQuantityText + "\nDiscount : " + discount + "\nEndTime : " + endTime;
                            itemList.add(new Item(ItemName, Integer.parseInt(ItemCostText), discount, companyName, companyAddress, Integer.parseInt(ItemQuantityText), endTime));
                            createNewItem += 1;
                            setNewItem = false;
                            break;
                        default:
                            ItemName = "";
                            ItemCost = 0;
                            ItemQuantity = 0;
                            endTime = "";
                            break;
                    }
                } else if (purchaseMode == true){

                    switch(purchaseType) {
                        case 1:
                            itemIndex = Integer.parseInt(message_text);
                            if (itemIndex > itemList.size()) {
                                reply = "Wrong Index";
                                purchaseType = 1;
                                purchaseMode = false;
                            } else if (itemList.get(itemIndex - 1).getQuantity() == 0) {
                                reply = "FULLY PURCHASED";
                                purchaseType = 1;
                                purchaseMode = false;
                            } else {
                                reply = "How many do you want buy?";
                                purchaseType = 2;
                            }
                            break;
                        case 2:
                            try {
                                ItemQuantity = Integer.parseInt(message_text);
                                if (ItemQuantity > itemList.get(itemIndex - 1).getQuantity()) {
                                    reply = "Wrong Quantity, restart";
                                } else {
                                    itemList.get(itemIndex - 1).purchase(ItemQuantity);
                                    reply = "What deliveryMode do you want? Press 1 for Self Collection, Press 2 for Delivery.";
                                    purchaseType = 3;
                                }
                            } catch (Exception e) {
                                reply = "Wrong Quantity";
                                purchaseType = 1;
                            }
                            break;
                        case 3:
                            try {
                                deliveryMode = Integer.parseInt(message_text);
                                if (deliveryMode == 1) {
                                    reply = "Self Collection selected";
                                } else if (deliveryMode == 2) {
                                    reply = "Delivery selected";
                                } else {
                                    reply = "I do not understand which deliveryMode you are looking for. Self Collection selected automatically";
                                }
                                reply += reply = "\npurchased. Check out your purchase /LINK \nDo you want to purchase more?\n/buy to purchase";
                                purchaseType = 1;
                                purchaseMode = false;
                            } catch (Exception e) {
                                reply = "There is something wrong, restarting";
                                purchaseType = 1;
                            }
                            break;
                    }

                } else if (!setCompanyName && !setNewItem) {
                    switch (message_text) {
                        case "/start":
                            reply = "Welcome! Are you a /vendor or /customer?";
                            break;
                        case "/vendor":
                            userType = 1;
                            reply = "Please state your company name:";
                            setCompanyName = true;
                            break;
                        case "/addItem":
                            if (userType == 1) {
                                reply = "What is the item you want to add?";
                                createNewItem = 1;
                                setNewItem = true;
                            } else {
                                reply = "You have no rights for this!";
                            }
                            break;
                        case "/customer":
                            userType = 2;
                            reply = "Would you like to check out the items? /list";
                            break;
                        case "/list":
                            reply = "";
                            try {
                                int index = 1;
                                for (Item item : itemList) {
                                    reply += "\n[" + index + "]\n" + item.toString();
                                    index += 1;
                                }
                                if (itemList.isEmpty()){
                                    reply = "Empty List!";
                                }
                            } catch (Exception e) {
                                reply = "There's something with the /list function";
                            }
                            break;
                        case "/gen":
                            for (int i = 0; i < 10; i++) {
                                String in = Integer.toString(i);
                                list.add(in);
                                itemList.add(new Item(in + "name", i * 10, i *5, companyName, companyName + " Street", i, in + "endTime"));
                            }
                            reply = "";
                            int index = 1;
                            for (Item item : itemList) {
                                reply += "\n[" + index + "]\n" + item.toString();
                                index += 1;
                            }
                            break;
                      case "/clear":
                            itemList.clear();
                            reply = "Your items are cleared";
                            break;
                        case "/buy":
                            purchaseMode = true;
                            purchaseType = 1;
                            reply = "Choose number on list to select buy";
                            break;
                        case "/sortByDiscount":
                            Collections.sort(itemList, new ItemDiscountComparator());
                            reply = "sorted by discount";
                            break;
                        case "/sortByPrice":
                            Collections.sort(itemList, new ItemPriceComparator());
                            reply = "sorted by price";
                            break;
                        case "/help":
                            reply = "Help Menu\n"
                                    + "/sortByDiscount\n"
                                    + "/sortByPrice\n"
                                    + "/gen\n"
                                    + "/addItem\n"
                                    + "/list\n";
                            break;
                        default:
                            reply = "I do not understand what you are saying";
                            break;
                    }
                }
            } catch (Exception e) {
                    reply = "There is something wrong with the system. Please try again.";
            }
                message.setText(reply);
                try {
                    sendMessage(message); // Sending our- message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
    }

        @Override
    public String getBotUsername() {
        return "foodrescue_bot";
    }

    @Override
    public String getBotToken() {
        return "809321281:AAGwaoGrgxLdE4PrwVJumblCzq5VVNNkIpg";
    }
}

class ItemDiscountComparator implements Comparator<Item>{

    @Override
    public int compare(Item item1, Item item2) {
        if( item1.getDiscount() < item2.getDiscount()){
            return 1;
        } else if( item1.getDiscount() > item2.getDiscount() ){
            return -1;
        }else{
            return 0;
        }
    }
}

class ItemPriceComparator implements Comparator<Item>{

    @Override
    public int compare(Item item1, Item item2) {
        if( item1.getItemNewCost() > item2.getItemNewCost()){
            return 1;
        } else if( item1.getItemNewCost() < item2.getItemNewCost() ){
            return -1;
        }else{
            return 0;
        }
    }
}