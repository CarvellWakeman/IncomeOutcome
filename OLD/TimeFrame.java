package carvellwakeman.incomeoutcome;

import java.util.ArrayList;

public class TimeFrame
{
    //RollOver time
    private TimePeriod timePeriod;

    //Income sources
    private ArrayList<Income> IncomeSources;

    //Expenses sources
    private ArrayList<Expense> ExpenseSources;


    public TimeFrame(){
        timePeriod = new TimePeriod();

        IncomeSources = new ArrayList<>();
        ExpenseSources = new ArrayList<>();

    }


    //Accessors
    public TimePeriod GetTimePeriod() { return timePeriod; }
    public int GetIncomeSourcesSize() { return IncomeSources.size(); }
    public int GetExpenseSourcesSize() { return ExpenseSources.size(); }

    //Mutators
    public void SetTimePeriod(TimePeriod tp) { timePeriod = tp; }


    //Income management
    public void AddIncome(Income income) { IncomeSources.add(income); }

    //Get Income at index
    public Income GetIncome(int index) { if (index >= 0 && IncomeSources.size() > 0){ return IncomeSources.get(index); } else { return null; } }

    //Get Income with ID
    public Income GetIncome(String id) {
        for (int i = 0; i < IncomeSources.size(); i++) {
            if (IncomeSources.get(i).toString().equals(id)) {
                return IncomeSources.get(i);
            }
        }
        return null;
    }


    //Expense management
    public void AddExpense(Expense expense) { ExpenseSources.add(expense); }

    //Get Income at index
    public Expense GetExpense(int index) { if (index >= 0 && ExpenseSources.size() > 0){ return ExpenseSources.get(index); } else { return null; } }

    //Get Income with ID
    public Expense GetExpense(String id) {
        for (int i = 0; i < ExpenseSources.size(); i++) {
            if (ExpenseSources.get(i).toString().equals(id)) {
                return ExpenseSources.get(i);
            }
        }
        return null;
    }

}
