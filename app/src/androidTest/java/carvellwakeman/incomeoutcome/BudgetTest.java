package carvellwakeman.incomeoutcome;

import android.content.Context;
import android.util.Log;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BudgetTest {
    Context mContext;

    @Before
    public void Setup(){
        mContext = App.GetContext();
    }



    @Test
    public void getID() throws Exception {
        Budget bud1 = new Budget("A");
        Budget bud2 = new Budget("B");

        // Budget IDs are unique when a budget is made
        assertTrue(bud1.GetID() != bud2.GetID());

        // Transaction IDs are positive integers
        assertTrue(bud1.GetID() > 0);
        assertTrue(bud2.GetID() > 0);

        // They can be set (but shouldn't often)
        bud1.SetID(bud2.GetID());
        assertTrue(bud1.GetID() == bud2.GetID());
    }

    @Test
    public void getName() throws Exception {
        Budget bud = new Budget("TestBud");
        assertTrue( bud.GetName().equals("TestBud") );

        bud.SetName("Budder");
        assertTrue( bud.GetName().equals("Budder") );
    }

    @Test
    public void getSelected() throws Exception {
        Budget bud1 = new Budget("A");
        Budget bud2 = new Budget("B");

        assertFalse(bud1.GetSelected());
        assertFalse(bud2.GetSelected());

        bud1.SetSelected(true);
        assertTrue(bud1.GetSelected());
        assertFalse(bud2.GetSelected());

        bud2.SetSelected(true);
        assertTrue(bud1.GetSelected());
        assertTrue(bud2.GetSelected());
    }

    @Test
    public void getPeriod() throws Exception {
        Budget bud = new Budget("broke");

        // Budgets default to month period
        assertTrue(bud.GetPeriod().getMonths() == 1);

        bud.SetPeriod(new Period(0,0,2,0,0,0,0,0));

        assertTrue(bud.GetPeriod().getMonths() == 0);
        assertTrue(bud.GetPeriod().getWeeks() == 2);

    }

    @Test
    public void getPeriodFreqency() throws Exception {
        Budget bud = new Budget("broke");

        bud.SetPeriod(new Period(1,0,0,0,0,0,0,0));
        assertTrue(bud.GetPeriodFreqency() == TimePeriod.Repeat.YEARLY);
    }

    @Test
    public void moveTimePeriod() throws Exception {
        Budget bud = new Budget("broke");
        bud.SetStartDate(new LocalDate(2017, 8, 1));
        bud.SetEndDate(new LocalDate(2017, 8, 31));

        bud.SetPeriod(new Period(1,0,0,0,0,0,0,0));

        // Start date initially correct
        assertTrue(bud.GetStartDate().equals(new LocalDate(2017, 8, 1)));

        // Move forward one year
        bud.MoveTimePeriod(1);
        assertTrue(bud.GetStartDate().equals(new LocalDate(2018, 8, 1)));

        // Move back one year
        bud.MoveTimePeriod(-1);
        assertTrue(bud.GetStartDate().equals(new LocalDate(2017, 8, 1)));

        // Move forward one month
        bud.SetPeriod(new Period(0,1,0,0,0,0,0,0));
        bud.MoveTimePeriod(1);
        assertTrue(bud.GetStartDate().equals(new LocalDate(2017, 9, 1)));

        // Move back one month
        bud.MoveTimePeriod(-1);
        assertTrue(bud.GetStartDate().equals(new LocalDate(2017, 8, 1)));
    }

    @Test
    public void addTransaction() throws Exception {
        Budget bud = new Budget("broke");
        Transaction tran1 = new Transaction();
        Transaction tran2 = new Transaction();

        // No transactions at first
        assertTrue( bud.GetTransactionCount() == 0 );

        // Add one transaction
        bud.AddTransaction(tran1);
        assertTrue( bud.GetTransactionCount() == 1 );
        assertTrue( bud.GetTransaction(tran1.GetID()) != null );
        assertTrue( bud.GetAllTransactions().get(0) != null );

        // Add another transaction
        bud.AddTransaction(tran2);
        assertTrue( bud.GetTransactionCount() == 2 );
        assertTrue( bud.GetTransaction(tran2.GetID()) != null );
        assertTrue( bud.GetAllTransactions().get(1) != null );

    }

    @Test
    public void removeTransaction() throws Exception {
        Budget bud = new Budget("broke");
        Transaction tran1 = new Transaction();

        // Add one transaction
        bud.AddTransaction(tran1);
        assertTrue( bud.GetTransactionCount() == 1 );
        assertTrue( bud.GetTransaction(tran1.GetID()) != null );

        bud.RemoveTransaction(tran1);
        assertTrue( bud.GetTransactionCount() == 0 );
        assertTrue( bud.GetTransaction(tran1.GetID()) == null );
    }

    @Test
    public void removeAllTransactions() throws Exception {
        Budget bud = new Budget("broke");
        Transaction tran1 = new Transaction();
        Transaction tran2 = new Transaction();

        // Add one transaction
        bud.AddTransaction(tran1);
        bud.AddTransaction(tran2);

        // Two transactions
        assertTrue( bud.GetTransactionCount() == 2 );
        assertTrue( bud.GetTransaction(tran1.GetID()) != null );
        assertTrue( bud.GetTransaction(tran2.GetID()) != null );

        bud.RemoveAllTransactions();
        assertTrue( bud.GetTransactionCount() == 0 );
    }

    @Test
    public void getTransaction() throws Exception {
        Budget bud = new Budget("broke");
        Transaction tran1 = new Transaction();
        Transaction tran2 = new Transaction();

        // Add one transaction
        bud.AddTransaction(tran1);
        bud.AddTransaction(tran2);

        // Two transactions
        assertTrue( bud.GetTransactionCount() == 2 );
        assertTrue( bud.GetTransaction(tran1.GetID()) != null );
        assertTrue( bud.GetTransaction(tran2.GetID()) != null );

        bud.RemoveAllTransactions();
        assertTrue( bud.GetTransactionCount() == 0 );
    }

    @Test
    public void getTransactions() throws Exception {
        Budget bud = new Budget("broke");
        Transaction tran1 = new Transaction();
        Transaction tran2 = new Transaction();

        tran1.SetTimePeriod(new TimePeriod(new LocalDate(2017, 8, 1)));
        tran2.SetTimePeriod(new TimePeriod(new LocalDate(2017, 8, 10)));

        // Add one transaction
        bud.AddTransaction(tran1);
        bud.AddTransaction(tran2);

        assertTrue( bud.GetAllTransactions().contains(tran1) );
        assertTrue( bud.GetAllTransactions().contains(tran2) );

        bud.SetStartDate(new LocalDate(2017, 8, 2));
        bud.SetEndDate(new LocalDate(2017, 8, 17));

        assertFalse( bud.GetTransactionsInTimeframe(mContext, Transaction.TRANSACTION_TYPE.Expense).contains(tran1) );
        assertTrue( bud.GetTransactionsInTimeframe(mContext, Transaction.TRANSACTION_TYPE.Expense).contains(tran2) );
    }

}