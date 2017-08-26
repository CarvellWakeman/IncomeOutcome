STAGING:
	Making Transaction totals working
	Paidback working (not saving in database )
	Sorting working
	Filtering working
	Totals bugfix
	Added show all
	Edge case for deleted people and categories
	Overview page
	Transactions split values not updated when total value is updated for non split transaction
	
	NEXT:Test, lint pass, lower api test
	

TODO
-High
*[BUGFIX] Cannot delete categories or people that are in use by transactions (Provide user with choice - change all to _ or delete all?)
*[BUGFIX] Handle when people are deleted but are used by a transaction
*[BUGFIX] Transaction Split rows (in details activity) not updating when some changes are made
*[BUGFIX] Importing a transaction should be done in the background
*[BUGFIX] Transaction parentID sometimes set to non-zero even when transaction has no parent. Clone Transaction? Result:overflow menu won't open
-Medium
*[BUGFIX] Versus Card not showing values when an even number of periods are shown
-Low
*[BUGFIX] Change to TextInputEditText instead of EditText
*[BUGFIX] Permissions revoked on runtime leaves import activity empty
*[BUGFIX] Some MyTab import expenses are not split

*[CHANGE] SplitAdapter has a data structure (class) that represents the holder's values. That data structure is updated (Don't hold references to viewHolders)
*[CHANGE] Transaction uses category ID and person ID instead of strings
*[CHANGE] Database operations use service/intentservice instead of AsyncTask (why?)
*[CHANGE] App theme colors
*[CHANGE] Vector drawables used instead of bitmaps, Pre API 21?
*[CHANGE] Transactions that occur in the future are now greyed out OR Divider line between transactions that is labeled as "Today"

*[ADD] Import data using file picker
*[ADD] Use Butterknife
*[ADD] Snackbar notifications for database functions / Notification of progress when async deleting data
*[ADD] Snackbar notifications for object delete (undo function?)
*[ADD] Per Person Payback Dates
*[ADD] Time period option "All Unpaid"
*[ADD] Toolbar changes color when current day is within timeperiod (Or some other indicator)
*[ADD] Overview of category/source
*[ADD] Rate in playstore button on Changelog
*[ADD] Android 7.1 app shortcuts
*[ADD] Testing framework
*[ADD] Tool to convert repeating individual transactions
*[ADD] Row Layout Transaction expand animation
*[ADD] Card Transaction Bar Charts
*[ADD] Add transaction widget
*[ADD] Itemized transactions
*[ADD] Export to google drive
*[ADD] Automatic backups
*[ADD] Support for equations in cost editText (14.99 + 2.35 = 17.34)
*[ADD] Show hints in app on first run
*[ADD] Source field now gives suggestions of nearby businesses queried through google and user history
*[ADD] Android Pay integration, transaction add notification given when Android Pay purchase is detected


UNRELEASED
*[ADD] Transactions can now be split between many people
*[ADD] Added categories Parking, membership
*[ADD] Overview activity how has a button to add transactions
#[ADD] Updated app throughout so that '-1' represents 'you', instead of zero in some places, and -1 in others

*[BUGFIX] Database manager correctly loads transactions that are not split
*[BUGFIX] Fixed crash when accessing data before database has completed loading or importing
#[BUGFIX] PaidBy radio button in new transaction not being checked initially (and not being saved when 'you' paid)
#[BUGFIX] Database not saving timeperiod UID

*[CHANGE] Major rewrite of data structures and database, UI updates on every page
*[CHANGE] Active filters are now displayed in the toolbar as a summary block
*[CHANGE] Show All moved to overflow menu
*[CHANGE] When adding a transaction, the keyboard hides when any element that does not use it is clicked
*[CHANGE] Refactored budget, category, and people management
*[CHANGE] CardTransaction clicking a slice of the pie chart highlights in the legend
*[CHANGE] Transaction card has legend view by default, clicking a slice of the chart shows the title of the transaction
*[CHANGE] Profiles are now refered to as budgets
#[CHANGE] DatabaseHelper contains method runDBTask which runs DB tasks Asynchronously, don't expose GenericAsyncTask


FORMAT
Ver #.#.# CHANNEL YYYY-MM-DD
-This is a note

*[BUGFIX] This is a bugfix

*[CHANGE] This is a change

*[ADD] This is an addition

*[DEL] This is a deletion

#[ADD] This is an addition that is not displayed to the user