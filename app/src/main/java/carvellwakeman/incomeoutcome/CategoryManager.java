package carvellwakeman.incomeoutcome;

import android.graphics.Color;
import java.util.ArrayList;

public class CategoryManager
{
    static CategoryManager instance = new CategoryManager();

    //Categories
    private ArrayList<Category> _categories;

    //Constructor and Init
    private CategoryManager(){}
    static CategoryManager getInstance(){ return instance; }
    public void initialize(){
        _categories = new ArrayList<>();
    }


    //Default categories
    public void LoadDefaultCategories(){
        AddCategory("Groceries", Color.argb(255, 0, 0, 255));
        AddCategory("Fast Food", Color.argb(255, 0, 20, 200));
        AddCategory("Restaurant", Color.argb(255, 50, 50, 150));
        AddCategory("Snacks", Color.argb(255, 50, 80, 150));
        AddCategory("Alcohol", Color.argb(255,60,60,60));

        AddCategory("Rent", Color.argb(255, 200, 0, 0));
        AddCategory("Mortgage", Color.argb(255, 180, 30, 0));
        AddCategory("Hotel", Color.argb(255, 180, 70, 50));

        AddCategory("ATM Withdrawal", Color.argb(255, 150, 50, 0));

        AddCategory("Electricity", Color.argb(255, 250, 255, 30));
        AddCategory("Sewer", Color.argb(255, 165, 165, 60));
        AddCategory("Water", Color.argb(255, 36, 174, 212));
        AddCategory("Garbage", Color.argb(255, 62, 105, 54));
        AddCategory("Internet", Color.argb(255, 50, 50, 150));
        AddCategory("Entertainment", Color.argb(255, 180, 255, 120));

        AddCategory("Gasoline", Color.argb(255, 150, 0, 150));
        AddCategory("Transportation", Color.argb(255, 230, 50, 255));
        AddCategory("Vehicle", Color.argb(255, 85, 0, 80));
        AddCategory("Parking", Color.argb(255, 240, 120, 250));

        AddCategory("Office Supplies", Color.argb(255, 255, 180, 80));
        AddCategory("Home Supplies", Color.argb(255, 255, 100, 40));
        AddCategory("Kitchen Supplies", Color.argb(255, 255, 50, 20));
        AddCategory("Home Improvement", Color.argb(255, 50, 0, 255));
        AddCategory("Home Repair", Color.argb(255, 115, 80, 255));
        AddCategory("Pet", Color.argb(255, 200, 255, 200));

        AddCategory("Hobbies", Color.argb(255, 30, 200, 80));
        AddCategory("Second-Hand", Color.argb(255, 15, 140, 50));
        AddCategory("Clothing/Jewelry", Color.argb(255, 20, 200, 5));

        AddCategory("Gifts", Color.argb(255, 0, 255, 255));
        AddCategory("Membership", Color.argb(255, 200, 190, 10));
        AddCategory("Subscription", Color.argb(255, 250, 250, 20));

        AddCategory("Medical", Color.argb(255, 130, 0, 40));
        AddCategory("Prescription", Color.argb(255, 180, 45, 50));
        AddCategory("Health & Beauty", Color.argb(255, 100, 0, 200));
        AddCategory("Personal", Color.argb(255, 216, 66, 216));

        AddCategory("Other", Color.argb(255, 140, 140, 140));
    }

    //Management
    public void AddCategory(Category category) {
        if (category != null) {
            Category cat = GetCategory(category.GetID());
            if (cat != null){ //Update
                cat.SetTitle(category.GetTitle());
                cat.SetColor(category.GetColor()); }
            else { //Add new
                _categories.add(category);
            }
        }
    }
    public void AddCategory(String title, int color){
        Category cat = GetCategory(title);
        if (cat != null){ cat.SetColor(color); }
        else {  AddCategory(new Category(title, color)); }
    }

    public void RemoveCategory(Category category) { _categories.remove(category); }
    public void RemoveCategory(String title) {
        for (int i = 0; i < _categories.size(); i++) {
            if (_categories.get(i).GetTitle().equals(title)) {
                RemoveCategory(_categories.get(i));
            }
        }
    }
    public void RemoveAllCategories() { _categories.clear(); }

    //Get category by index
    //public boolean HasCategory(String category){
    //    for (int i = 0; i < _categories.size(); i++) {
    //        if (_categories.get(i).GetTitle().equals(category)) {
    //            return true;
    //        }
    //    }
    //    return false;
    //}
    public Category GetCategory(int ID){
        for (Category cat : _categories) {
            if (cat.GetID() == ID) { return cat; }
        }
        return null;
    }
    public Category GetCategory(String title) {
        for (Category cat : _categories) {
            if (cat.GetTitle().equals(title)) { return cat; }
        }
        return null;
    }

    public ArrayList<Category> GetCategories() { return _categories; }
    public ArrayList<String> GetCategoriesTitles(){
        ArrayList<String> arr = new ArrayList<>();
        for (Category cat : _categories) {
            arr.add(cat.GetTitle());
        }
        return arr;
    }

    public int GetCategoriesCount() { return _categories.size(); }

}
