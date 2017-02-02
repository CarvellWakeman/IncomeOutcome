package carvellwakeman.incomeoutcome;



import java.util.ArrayList;

public class BudgetManager
{
    static BudgetManager instance = new BudgetManager();

    //Budgets
    private ArrayList<Budget> _budgets;


    //Constructor and Init
    private BudgetManager(){}
    static BudgetManager getInstance(){ return instance; }

    public void initialize()
    {
        _budgets = new ArrayList<>();
    }

    //Budget selection
    public void SetSelectedBudget(Budget budget){
        for (Budget b : GetBudgets()){
            b.SetSelected(false);
        }
        budget.SetSelected(true);
    }

    public Budget GetSelectedBudget(){
        for (Budget b : GetBudgets()){
            if (b.GetSelected()){ return b; }
        }
        return null;
    }


    //Budget Management
    public Budget AddBudget(Budget budget) {
        if (budget != null) {
            Budget cat = GetBudget(budget.GetID());
            if (cat != null) { //Update
                cat.SetName(budget.GetName());
                cat.SetStartDate(budget.GetStartDate());
                cat.SetEndDate(budget.GetEndDate());
                cat.SetPeriod(budget.GetPeriod());
                cat.SetSelected(budget.GetSelected());
            } else { //Add new
                _budgets.add(budget);
            }
        }
        return budget;
    }

    public void RemoveBudget(Budget budget)
    {
        if (budget != null) {
            budget.RemoveAllTransactions();
            _budgets.remove(budget);
        }
    }
    public void RemoveBudget(int ID) {
        for (int i = 0; i < _budgets.size(); i++) {
            if (_budgets.get(i).GetID() == ID){
                _budgets.get(i).RemoveAllTransactions();
                _budgets.remove(i);
            }
        }
    }
    public void RemoveAllBudgets(){
        for (int i = 0; i < _budgets.size(); i++) {
            _budgets.get(i).RemoveAllTransactions();
        }

        _budgets.clear();
    }

    public Budget GetBudget(int ID) {
        for (int i = 0; i < _budgets.size(); i++) {
            if (_budgets.get(i).GetID() == ID) {
                return _budgets.get(i);
            }
        }
        return null;
    }
    public Budget GetBudget(String name){
        for (int i = 0; i < _budgets.size(); i++) {
            if (_budgets.get(i).GetName().equals(name)) {
                return _budgets.get(i);
            }
        }
        return null;
    }

    public ArrayList<Budget> GetBudgets(){ return _budgets; }

    public int GetBudgetCount() { return _budgets.size(); }

}
