package carvellwakeman.incomeoutcome;


import android.content.Context;
import android.os.LocaleList;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class TransactionTest {

    Context mContext;


    @Before
    public void Setup(){
        mContext = App.GetContext();
    }



    @Test
    public void type() throws Exception {
        Transaction tran = new Transaction();

        // Default type is expense
        assertTrue(tran.GetType() == Transaction.TRANSACTION_TYPE.Expense);

        // Set to income
        tran.SetType(Transaction.TRANSACTION_TYPE.Income);
        assertTrue(tran.GetType() == Transaction.TRANSACTION_TYPE.Income);

        // Set to expense
        tran.SetType(Transaction.TRANSACTION_TYPE.Expense);
        assertTrue(tran.GetType() == Transaction.TRANSACTION_TYPE.Expense);

        // Null is null
        tran.SetType(null);
        assertTrue(tran.GetType() == null);

        // Copying
        assertTrue(new Transaction(tran).GetType() == tran.GetType());
    }

    @Test
    public void ID() throws Exception {
        Transaction tran1 = new Transaction();
        Transaction tran2 = new Transaction();

        // Transaction IDs are unique when a transaction is made
        assertTrue(tran1.GetID() != tran2.GetID());

        // Transaction IDs are positive integers
        assertTrue(tran1.GetID() > 0);
        assertTrue(tran2.GetID() > 0);

        // They can be set (but shouldn't often)
        tran1.SetID(tran2.GetID());
        assertTrue(tran1.GetID() == tran2.GetID());

        // Copied transaction IDs are still unique
        assertTrue(new Transaction(tran1).GetID() != tran1.GetID());
    }

    @Test
    public void parentID() throws Exception {
        Transaction parent = new Transaction();
        Transaction child = new Transaction();

        // Transaction IDs are unique at conception
        assertTrue(parent.GetID() != child.GetID());

        // Transaction parent IDs are zero at conception
        assertTrue(parent.GetParentID() == 0);
        assertTrue(child.GetParentID() == 0);

        // Adoption
        child.SetParentID(parent.GetID());
        assertTrue(parent.GetID() != child.GetID());
        assertTrue(parent.GetID() == child.GetParentID());
        assertTrue(parent.GetParentID() != child.GetID());
    }

    @Test
    public void budgetID() throws Exception {
        Budget budget = new Budget("IHaveNoMoney");
        Transaction tran = new Transaction();

        // Budget IDs default positive int
        assertTrue(budget.GetID() > 0);

        // Transaction budget IDs default zero
        assertTrue(tran.GetBudgetID() == 0);

        // Assimilation
        budget.AddTransaction(tran);
        assertTrue(tran.GetBudgetID() == budget.GetID());

        // Transaction is stored in budget
        assertTrue(budget.GetAllTransactions().get(0).GetBudgetID() == budget.GetID());
        assertTrue(budget.GetAllTransactions().get(0).GetID() == tran.GetID());

        // Budget ID can be set manually
        tran.SetBudgetID(0);
        assertTrue(tran.GetBudgetID() != budget.GetID());
    }

    @Test
    public void value() throws Exception {
        Transaction tran = new Transaction();

        // Transaction value defaults to zero
        assertTrue(tran.GetValue() == 0.0d);

        // Transaction value can be set
        tran.SetValue(123.4d);
        assertTrue(tran.GetValue() == 123.4d);
    }

    @Test
    public void split() throws Exception {
        Transaction tran = new Transaction();
        Person A = new Person("Apple");

        // Initial values
        tran.SetValue(100.0d);
        tran.SetSplit(Person.Me.GetID(), tran.GetValue());

        // Initial split with 'you' should be 100%
        assertTrue(tran.GetSplit(Person.Me.GetID()) == tran.GetValue());
        assertTrue(tran.GetSplit(Person.Me.GetID()) == 100.0d);

        // Split 25% with apple
        tran.SetSplit(A.GetID(), tran.GetValue() * 0.25);
        assertTrue(tran.GetSplit(A.GetID()) == tran.GetValue() * 0.25);
    }

    @Test
    public void splitPercentage() throws Exception {
        Transaction tran = new Transaction();
        Person A = new Person("Apple");

        // Initial values
        tran.SetValue(100.0d);
        tran.SetSplit(Person.Me.GetID(), 25.0d);
        tran.SetSplit(A.GetID(), 75.0d);

        // Your split should be 25%
        assertTrue(tran.GetSplitPercentage(Person.Me.GetID()) == 25.0d);

        // Apple's split should be 75%
        assertTrue(tran.GetSplitPercentage(A.GetID()) == 75.0d);
    }

    @Test
    public void isSplit() throws Exception {
        Transaction tran = new Transaction();
        Person A = new Person("Apple");

        // Initial values
        tran.SetValue(100.0d);

        // Transaction is not split unless more than one person are included
        assertFalse(tran.IsSplit());

        // Include one person (you) in the split
        tran.SetSplit(Person.Me.GetID(), tran.GetValue()/2);

        // Transaction is not split unless more than one person are included
        assertFalse(tran.IsSplit());

        // Include apple in the split
        tran.SetSplit(A.GetID(), tran.GetValue()/2);

        // Transaction is split when two people are included
        assertTrue(tran.IsSplit());
    }

    @Test
    public void splitArray() throws Exception {
        Transaction tran = new Transaction();
        Person A = new Person("Apple");
        Person B = new Person("Banana");
        Person C = new Person("Carrot");

        // Initial values
        tran.SetValue(100.0d);

        // Split array should be empty
        assertTrue( tran.GetSplitArray().size() == 0 );

        // Split transaction with me only
        tran.SetSplit(Person.Me.GetID(), 25.0d);

        // Split array should have one element 'you'
        assertTrue( tran.GetSplitArray().size() == 1 );
        assertTrue( tran.GetSplitArray().containsKey(Person.Me.GetID()) );

        // Split with two more people
        tran.SetSplit(A.GetID(), 24.0d);
        tran.SetSplit(B.GetID(), 26.0d);

        // Split array should show the added people
        assertTrue( tran.GetSplitArray().size() == 3 );
        assertTrue( tran.GetSplitArray().containsKey(Person.Me.GetID()) );
        assertTrue( tran.GetSplitArray().containsKey(A.GetID()) );
        assertTrue( tran.GetSplitArray().containsKey(B.GetID()) );

        assertTrue( tran.GetSplitArray().get(Person.Me.GetID()) == 25.0d );
        assertTrue( tran.GetSplitArray().get(A.GetID()) == 24.0d  );
        assertTrue( tran.GetSplitArray().get(B.GetID()) == 26.0d  );
    }

    @Test
    public void splitArrayString() throws Exception {
        Transaction tran = new Transaction();
        Person A = new Person("Apple");
        Person B = new Person("Banana");
        Person C = new Person("Carrot");

        // Initial values
        tran.SetValue(100.0d);

        // Split transaction
        tran.SetSplit(Person.Me.GetID(), 25.0d);
        tran.SetSplit(A.GetID(), 24.0d);
        tran.SetSplit(B.GetID(), 26.0d);
        tran.SetSplit(C.GetID(), 25.0d);

        // Split array string should format for saving
        String expStrMe = String.valueOf(Person.Me.GetID()) + ":" + tran.GetSplit(Person.Me.GetID());
        String expStrA = String.valueOf(A.GetID()) + ":" + tran.GetSplit(A.GetID());
        String expStrB = String.valueOf(B.GetID()) + ":" + tran.GetSplit(B.GetID());
        String expStrC = String.valueOf(C.GetID()) + ":" + tran.GetSplit(C.GetID());

        assertTrue( (tran.GetSplitArrayString().length() - tran.GetSplitArrayString().replace("|", "").length()) == 3 ); // Three |'s should be included
        assertTrue( tran.GetSplitArrayString().contains(expStrMe) );
        assertTrue( tran.GetSplitArrayString().contains(expStrA) );
        assertTrue( tran.GetSplitArrayString().contains(expStrB) );
        assertTrue( tran.GetSplitArrayString().contains(expStrC) );

    }

    @Test
    public void getDebt() throws Exception {
        Transaction tran = new Transaction();
        Person A = new Person("Apple");
        Person B = new Person("Banana");
        Person C = new Person("Carrot");

        // Initial values
        tran.SetValue(100.0d);

        // Split transaction
        tran.SetSplit(Person.Me.GetID(), 23.0d);
        tran.SetSplit(A.GetID(), 24.0d);
        tran.SetSplit(B.GetID(), 25.0d);
        tran.SetSplit(C.GetID(), 26.0d);

        tran.SetPaidBy(Person.Me.GetID());

        // Funky edge case, your debt to you should be zero?
        assertTrue( tran.GetDebt(Person.Me.GetID(), Person.Me.GetID()) == 0.0d );

        // Paid by you, debt to all others should be zero, debt from all others should be their split
        assertTrue( tran.GetDebt(Person.Me.GetID(), A.GetID()) == 0.0d );
        assertTrue( tran.GetDebt(Person.Me.GetID(), B.GetID()) == 0.0d );
        assertTrue( tran.GetDebt(Person.Me.GetID(), C.GetID()) == 0.0d );

        assertTrue( tran.GetDebt(A.GetID(), Person.Me.GetID()) == 24.0d );
        assertTrue( tran.GetDebt(B.GetID(), Person.Me.GetID()) == 25.0d );
        assertTrue( tran.GetDebt(C.GetID(), Person.Me.GetID()) == 26.0d );

        // Paid by A
        tran.SetPaidBy(A.GetID());
        assertTrue( tran.GetDebt(Person.Me.GetID(), A.GetID()) == 23.0d );
        assertTrue( tran.GetDebt(Person.Me.GetID(), B.GetID()) == 0.0d );
        assertTrue( tran.GetDebt(Person.Me.GetID(), C.GetID()) == 0.0d );

        // Paid by B
        tran.SetPaidBy(B.GetID());
        assertTrue( tran.GetDebt(Person.Me.GetID(), A.GetID()) == 0.0d );
        assertTrue( tran.GetDebt(Person.Me.GetID(), B.GetID()) == 23.0d );
        assertTrue( tran.GetDebt(Person.Me.GetID(), C.GetID()) == 0.0d );

        // Paid by C, C's debt to others should be zero, other's debt to c should be their split
        tran.SetPaidBy(C.GetID());
        assertTrue( tran.GetDebt(C.GetID(), Person.Me.GetID()) == 0.0d );
        assertTrue( tran.GetDebt(C.GetID(), A.GetID()) == 0.0d );
        assertTrue( tran.GetDebt(C.GetID(), B.GetID()) == 0.0d );

        assertTrue( tran.GetDebt(Person.Me.GetID(), C.GetID()) == 23.0d );
        assertTrue( tran.GetDebt(A.GetID(), C.GetID()) == 24.0d );
        assertTrue( tran.GetDebt(B.GetID(), C.GetID()) == 25.0d );

    }

    @Test
    public void paidBy() throws Exception {
        Transaction tran = new Transaction();
        Person A = new Person("Apple");


        // Initially paid by you
        assertTrue( tran.GetPaidBy() == Person.Me.GetID() );

        tran.SetPaidBy(A.GetID());

        // Paid by apple
        assertTrue( tran.GetPaidBy() == A.GetID() );
    }

    @Test
    public void paidBack() throws Exception {
        Transaction tran = new Transaction();

        // Initially paidBack is null
        assertTrue( tran.GetPaidBack() == null );

        // Paid back today
        tran.SetPaidBack(new LocalDate());
        assertTrue( tran.GetPaidBack().equals(new LocalDate()) );
    }

    @Test
    public void source() throws Exception {
        Transaction tran = new Transaction();
        tran.SetSource("TestSource");

        assertTrue( tran.GetSource().equals("TestSource") );
    }

    @Test
    public void description() throws Exception {
        Transaction tran = new Transaction();
        tran.SetDescription("TestDescription");

        assertTrue( tran.GetDescription().equals("TestDescription") );
    }

    @Test
    public void category() throws Exception {
        Transaction tran = new Transaction();
        Category cat = new Category("TestCat", Helper.getColor(R.color.red));

        tran.SetCategory(cat.GetID());

        assertTrue( tran.GetCategory() == cat.GetID() );
    }

    @Test
    public void timePeriod() throws Exception {
        Transaction tran = new Transaction();
        TimePeriod tp = new TimePeriod();

        tran.SetTimePeriod(tp);

        assertTrue( tran.GetTimePeriod() == tp );
    }

    @Test
    public void getOccurrences() throws Exception {
        Transaction tran = new Transaction();
        tran.SetType(Transaction.TRANSACTION_TYPE.Expense);

        TimePeriod tp;
        LocalDate start;
        LocalDate end;
        ArrayList<Transaction> trans;


        // Dates
        start = new LocalDate(2017, 8, 24);
        end = new LocalDate(2017, 8, 26);


        // Basic Daily Repeating timeperiod
        tp = new TimePeriod();
        tp.SetDate(start);
        tp.SetRepeatFrequency(TimePeriod.Repeat.DAILY);
        tran.SetTimePeriod(tp);

        trans = tran.GetOccurrences(start, end, Transaction.TRANSACTION_TYPE.Expense);
        assertTrue( trans.size() == 3 );
        assertTrue( trans.get(0).GetTimePeriod().GetDate().equals(start) );
        assertTrue( trans.get(1).GetTimePeriod().GetDate().equals(new LocalDate()) );
        assertTrue( trans.get(2).GetTimePeriod().GetDate().equals(end) );


        // Basic Monthly timeperiod
        start = new LocalDate(2017, 8, 1);
        end = new LocalDate(2017, 10, 30);
        tp = new TimePeriod();
        tp.SetDate(start);
        tp.SetRepeatFrequency(TimePeriod.Repeat.MONTHLY);
        tran.SetTimePeriod(tp);

        trans = tran.GetOccurrences(start, end, Transaction.TRANSACTION_TYPE.Expense);
        assertTrue( trans.size() == 3 );
        assertTrue( trans.get(0).GetTimePeriod().GetDate().equals(start) );
        assertTrue( trans.get(1).GetTimePeriod().GetDate().equals(new LocalDate(2017, 9, 1)) );
        assertTrue( trans.get(2).GetTimePeriod().GetDate().equals(new LocalDate(2017, 10, 1)) );

        // Monthly test with blacklisted date
        tp.AddBlacklistDate(tran.GetID(), new LocalDate(2017, 9, 1), false);

        trans = tran.GetOccurrences(start, end, Transaction.TRANSACTION_TYPE.Expense);
        assertTrue( trans.size() == 2 );
        assertTrue( trans.get(0).GetTimePeriod().GetDate().equals(start) );
        assertTrue( trans.get(1).GetTimePeriod().GetDate().equals(new LocalDate(2017, 10, 1)) );

    }

    @Test
    public void setSplitFromArrayString() throws Exception {
        Transaction tran = new Transaction();
        Person A = new Person("Apple");
        Person B = new Person("Banana");
        Person C = new Person("Carrot");

        // Initial values
        tran.SetValue(100.0d);

        // Split array string should format for saving
        String expStrMe = String.valueOf(Person.Me.GetID()) + ":" + String.valueOf(28.0);
        String expStrA = String.valueOf(A.GetID()) + ":" + String.valueOf(23.0);
        String expStrB = String.valueOf(B.GetID()) + ":" + String.valueOf(24.0);
        String expStrC = String.valueOf(C.GetID()) + ":" + String.valueOf(25.0);

        String splitStr = expStrMe + "|" + expStrA + "|" + expStrB + "|" + expStrC;

        tran.SetSplitFromArrayString(splitStr);

        // Test split was successful
        assertTrue( tran.IsSplit() );
        assertTrue( tran.GetSplitArray().size() == 4 );
        assertTrue( tran.GetSplit(Person.Me.GetID()) == 28.0d );
        assertTrue( tran.GetSplit(A.GetID()) == 23.0d );
        assertTrue( tran.GetSplit(B.GetID()) == 24.0d );
        assertTrue( tran.GetSplit(C.GetID()) == 25.0d );

    }


    @Test
    public void sortCompare() throws Exception {
        CategoryManager.getInstance().initialize();
        PersonManager.getInstance().initialize();

        Transaction tran1 = new Transaction();
        Transaction tran2 = new Transaction();

        TimePeriod tp1 = new TimePeriod();
        TimePeriod tp2 = new TimePeriod();

        Category cat1 = new Category("A", 0);
        Category cat2 = new Category("B", 0);
        CategoryManager.getInstance().AddCategory(cat1);
        CategoryManager.getInstance().AddCategory(cat2);

        Person per1 = new Person("A");
        Person per2 = new Person("B");
        PersonManager.getInstance().AddPerson(per1);
        PersonManager.getInstance().AddPerson(per2);

        tran1.SetTimePeriod(tp1);
        tran2.SetTimePeriod(tp2);

        tran1.SetCategory(cat1.GetID());
        tran2.SetCategory(cat2.GetID());


        // Test date sort
        tp1.SetDate(new LocalDate(2017, 8, 1));
        tp2.SetDate(new LocalDate(2017, 8, 2));

        assertTrue( tran1.SortCompare(tran2, Helper.SORT_METHODS.DATE_ASC) == 1 );
        assertTrue( tran1.SortCompare(tran2, Helper.SORT_METHODS.DATE_DSC) == -1 );


        // Test cost sort
        tran1.SetValue(1);
        tran2.SetValue(2);

        assertTrue( tran1.SortCompare(tran2, Helper.SORT_METHODS.COST_ASC) == 1 );
        assertTrue( tran1.SortCompare(tran2, Helper.SORT_METHODS.COST_DSC) == -1 );


        // Test category sort
        assertTrue( tran1.SortCompare(tran2, Helper.SORT_METHODS.CATEGORY_ASC) == 1 );
        assertTrue( tran1.SortCompare(tran2, Helper.SORT_METHODS.CATEGORY_DSC) == -1 );


        // Test source sort
        tran1.SetSource("A");
        tran2.SetSource("B");

        assertTrue( tran1.SortCompare(tran2, Helper.SORT_METHODS.SOURCE_ASC) == 1 );
        assertTrue( tran1.SortCompare(tran2, Helper.SORT_METHODS.SOURCE_DSC) == -1 );


        // Test paidby sort
        tran1.SetPaidBy(per1.GetID());
        tran2.SetPaidBy(per2.GetID());

        assertTrue( tran1.SortCompare(tran2, Helper.SORT_METHODS.PAIDBY_ASC) == 1 );
        assertTrue( tran1.SortCompare(tran2, Helper.SORT_METHODS.PAIDBY_DSC) == -1 );
    }

}