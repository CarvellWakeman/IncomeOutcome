STAGING:
	Testing, lower API pass
	
	NEXT:More testing, bug fixes, new features
	

TODO
-High
*[BUGFIX] Time Period objects should have their own manager and be passed by ID
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

*[CHANGE] All objects (models) should have a manager and only be related by foreign keys. Remove split column, blacklist dates column, etc.
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



FORMAT
Ver #.#.# CHANNEL YYYY-MM-DD
-This is a note

*[BUGFIX] This is a bugfix

*[CHANGE] This is a change

*[ADD] This is an addition

*[DEL] This is a deletion

#[ADD] This is an addition that is not displayed to the user