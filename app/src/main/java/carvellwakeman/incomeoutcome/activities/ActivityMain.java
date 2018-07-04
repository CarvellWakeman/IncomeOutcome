package carvellwakeman.incomeoutcome.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;
import carvellwakeman.incomeoutcome.App;
import carvellwakeman.incomeoutcome.FAB;
import carvellwakeman.incomeoutcome.MaterialSheetFabMod;
import carvellwakeman.incomeoutcome.R;
import carvellwakeman.incomeoutcome.data.BudgetManager;
import carvellwakeman.incomeoutcome.data.CategoryManager;
import carvellwakeman.incomeoutcome.data.DatabaseManager;
import carvellwakeman.incomeoutcome.data.PersonManager;
import carvellwakeman.incomeoutcome.dialogs.DialogFragmentChangelog;
import carvellwakeman.incomeoutcome.helpers.Helper;
import carvellwakeman.incomeoutcome.models.Budget;
import carvellwakeman.incomeoutcome.models.Transaction;
import carvellwakeman.incomeoutcome.viewmodels.CardTransaction;
import carvellwakeman.incomeoutcome.viewmodels.CardVersus;

import java.util.ArrayList;


public class ActivityMain extends AppCompatActivity
{
    Budget _selectedBudget;

    ArrayList<Integer> toolbar_menus;

    Toolbar toolbar;

    CardVersus versusCard;
    CardTransaction expensesCard;
    CardTransaction incomeCard;

    Button button_suggestaddbudget;

    FAB FAB_addNew;
    MaterialSheetFabMod materialSheetFab;

    RelativeLayout relativeLayout_period;
    ImageView button_nextPeriod;
    ImageView button_prevPeriod;

    LinearLayout progress_loadingData;
    LinearLayout FAB_newExpense;
    LinearLayout FAB_newIncome;


    //Managers
    DatabaseManager databaseManager;
    BudgetManager budgetManager;
    CategoryManager categoryManager;
    PersonManager personManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize Budget manager
        budgetManager = BudgetManager.getInstance();
        budgetManager.initialize();

        //Initialize category manager
        categoryManager = CategoryManager.getInstance();
        categoryManager.initialize();

        //Initialize otherperson manager
        personManager = PersonManager.getInstance();
        personManager.initialize();

        //Initialize the Database
        databaseManager = DatabaseManager.getInstance(ActivityMain.this);

        //Load data from database
        databaseManager.loadSettings( //Load settings
            new Runnable() {
                @Override public void run() {
                databaseManager._loadTransactions();
                //if (versusCard!=null){ versusCard.getBase().setVisibility(View.VISIBLE); }
                if (incomeCard!=null){ incomeCard.getBase().setVisibility(View.VISIBLE); }
                if (expensesCard!=null){ expensesCard.getBase().setVisibility(View.VISIBLE); }
                if (progress_loadingData!=null){ progress_loadingData.setVisibility(View.GONE); }
                if (relativeLayout_period!=null){ relativeLayout_period.setVisibility(View.VISIBLE); }

                //Selected budget
                _selectedBudget = budgetManager.GetSelectedBudget();

                RefreshActivity();
                }
            }
        );


        //Toolbar menus
        toolbar_menus = new ArrayList<>();
        toolbar_menus.add(R.menu.submenu_settings);


        //Find Views
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);

        button_suggestaddbudget = (Button) findViewById(R.id.button_suggest_add_budget);

        button_suggestaddbudget.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                intent = new Intent(ActivityMain.this, ActivityManageBudgets.class);
                intent.putExtra("addnew", true);
                startActivity(intent);
            }
        });

        //Period management
        relativeLayout_period = (RelativeLayout) findViewById(R.id.relativeLayout_toolbarPeriod);
        relativeLayout_period.setVisibility(View.GONE);
        button_nextPeriod = (ImageView) findViewById(R.id.button_nextPeriod);
        button_prevPeriod = (ImageView) findViewById(R.id.button_prevPeriod);

        //Loading
        progress_loadingData = (LinearLayout) findViewById(R.id.progress_database_loading);

        //Period forward/back
        button_nextPeriod.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (_selectedBudget != null){
                    _selectedBudget.MoveTimePeriod(1);
                    databaseManager._insertSetting(_selectedBudget, true);
                    RefreshActivity();
                }
            }
        });
        button_prevPeriod.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (_selectedBudget != null){
                    _selectedBudget.MoveTimePeriod(-1);
                    databaseManager._insertSetting(_selectedBudget, true);
                    RefreshActivity();
                }
            }
        });

        //Configure toolbar
        for(int m : toolbar_menus){ toolbar.inflateMenu(m); }
        setSupportActionBar(toolbar);


        //Card inflater
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.overview_layout);


        //Cards
        Integer budgetID = (_selectedBudget != null ? _selectedBudget.GetID() : 0);
        // = new CardVersus(insertPoint, 0, budgetID, this, inflater, R.layout.card_versus);
        expensesCard = new CardTransaction(this, insertPoint, 1, budgetID, Transaction.TRANSACTION_TYPE.Expense, inflater, R.layout.card_transaction);
        incomeCard = new CardTransaction(this, insertPoint, 2, budgetID, Transaction.TRANSACTION_TYPE.Income, inflater, R.layout.card_transaction);

        //versusCard.getBase().setVisibility(View.GONE);
        expensesCard.getBase().setVisibility(View.GONE);
        incomeCard.getBase().setVisibility(View.GONE);

        //versusCard.insert(insertPoint, 0);
        //expensesCard.insert(insertPoint, 1);
        //incomeCard.insert(insertPoint, 2);


        //Floating action button
        FAB_addNew = (FAB) findViewById(R.id.FAB_transaction_type_new);
        View sheetView = findViewById(R.id.fab_sheet);
        View overlay = findViewById(R.id.dim_overlay);
        materialSheetFab = new MaterialSheetFabMod<>(FAB_addNew, sheetView, overlay,
                getResources().getColor(R.color.white), getResources().getColor(R.color.colorAccent));

        FAB_newExpense = (LinearLayout) findViewById(R.id.linearLayout_new_expense);
        FAB_newIncome = (LinearLayout) findViewById(R.id.linearLayout_new_income);

        FAB_newExpense.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                materialSheetFab.hideSheet();
                if (_selectedBudget != null) {
                    Intent intent = new Intent(ActivityMain.this, ActivityNewTransaction.class);
                    intent.putExtra("activitytype", Transaction.TRANSACTION_TYPE.Expense.ordinal());
                    intent.putExtra("budget", _selectedBudget.GetID());
                    startActivity(intent);
                }
            }
        });
        FAB_newIncome.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                materialSheetFab.hideSheet();
                if (_selectedBudget != null) {
                    Intent intent = new Intent(ActivityMain.this, ActivityNewTransaction.class);
                    intent.putExtra("activitytype", Transaction.TRANSACTION_TYPE.Income.ordinal());
                    intent.putExtra("budget", _selectedBudget.GetID());
                    startActivity(intent);
                }
            }
        });


        //Check app version for update, display changelog
        if (!App.GetVersion(this).equals(App.GetPrevVersion())){
            App.SetLastVersion(this);
            Helper.OpenDialogFragment(this, DialogFragmentChangelog.newInstance(), true);
        }

    }


    @Override
    public void onResume(){
        super.onResume();

        RefreshActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        for(int m : toolbar_menus){ getMenuInflater().inflate(m, menu); }

        return true;
    }


    //Toolbar button handling
    Intent intent;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                return true;
            case R.id.toolbar_action_settings: //Start settings activity
                intent = new Intent(ActivityMain.this, ActivitySettings.class);
                startActivity(intent);
                return true;

            default:
                break;
        }

        return true;
    }


    //Send back a RESULT_OK to MainActivity when back is pressed
    @Override
    public void onBackPressed() {
        if (materialSheetFab.isSheetVisible()) {
            materialSheetFab.hideSheet();
        } else {
            super.onBackPressed();
        }
    }


    //Refresh overview
    public void RefreshActivity(){
        _selectedBudget = BudgetManager.getInstance().GetSelectedBudget();

        //Suggest the user add a budget if none exist
        if (_selectedBudget == null){
            //versusCard.getBase().setVisibility(View.GONE);
            expensesCard.getBase().setVisibility(View.GONE);
            incomeCard.getBase().setVisibility(View.GONE);
            button_suggestaddbudget.setVisibility(View.VISIBLE);
            progress_loadingData.setVisibility(View.GONE);
            relativeLayout_period.setVisibility(View.GONE);
            FAB_addNew.setVisibility(View.GONE);
        }
        else {
            //versusCard.getBase().setVisibility(View.VISIBLE);
            expensesCard.getBase().setVisibility(View.VISIBLE);
            incomeCard.getBase().setVisibility(View.VISIBLE);
            button_suggestaddbudget.setVisibility(View.GONE);
            relativeLayout_period.setVisibility(View.VISIBLE);
            FAB_addNew.setVisibility(View.VISIBLE);


            expensesCard.SetBudget(_selectedBudget.GetID());
            incomeCard.SetBudget(_selectedBudget.GetID());
            //versusCard.SetBudget(_selectedBudget.GetID());

            //versusCard.SetData();
            expensesCard.Refresh();
            incomeCard.Refresh();
        }

        ToolbarTitleUpdate();
    }

    //Toolbar title update
    public void ToolbarTitleUpdate(){
        if (getSupportActionBar() != null) {

            if (_selectedBudget != null) {
                getSupportActionBar().setTitle(_selectedBudget.GetName() + " " + getString(R.string.title_overview));
                getSupportActionBar().setSubtitle(_selectedBudget.GetDateFormatted(this));
            }
            else {
                getSupportActionBar().setTitle(R.string.title_overview);
                getSupportActionBar().setSubtitle("");
            }
        }
    }

}
