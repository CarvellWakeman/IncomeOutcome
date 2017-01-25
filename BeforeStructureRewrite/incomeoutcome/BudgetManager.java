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


    //Budget Management
    public void AddBudget(Budget budget) {
        if (budget != null) {
            _budgets.add(budget);
        }
    }

    public void RemoveBudget(Budget budget)
    {
        if (budget != null) {
            _budgets.remove(budget);
        }
    }
    public void RemoveBudget(int ID) {
        for (int i = 0; i < _budgets.size(); i++) {
            if (_budgets.get(i).GetID() == ID){
                _budgets.remove(i);
            }
        }
    }
    public void RemoveAllBudgets(){ _budgets.clear(); }

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
