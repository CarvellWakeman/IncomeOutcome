 /*
        //LocalDate timeFrame_start = null, timeFrame_end = null;
        LocalDate overlap_start = null, overlap_end = null;

        LocalDate event = null;
        LocalDate event_start = null, event_end = null;
        LocalDate event_lastOccurrence = null;

        int YEARS  = repeatEveryN * (repeatFrequency==Repeat.YEARLY ? 1 : 0);
        int MONTHS = repeatEveryN * (repeatFrequency==Repeat.MONTHLY ? 1 : 0);
        int WEEKS  = repeatEveryN * (repeatFrequency==Repeat.WEEKLY ? 1 : 0);
        int DAYS   = repeatEveryN * (repeatFrequency==Repeat.DAILY ? 1 : 0);
        int YEARS_TOTAL  = Math.max((YEARS * repeatUntilTimes) - 1, 0);
        int MONTHS_TOTAL = Math.max((MONTHS * repeatUntilTimes) - 1, 0);
        int WEEKS_TOTAL = Math.max((WEEKS * repeatUntilTimes) - 1, 0);
        int DAYS_TOTAL = Math.max((DAYS * repeatUntilTimes) - 1, 0);

        ProfileManager.PrintLong("Years:" + String.valueOf(YEARS) + "\nMonths:" + String.valueOf(MONTHS) + "\nWeeks:" + String.valueOf(WEEKS) + "\nDays:" + String.valueOf(DAYS));
        ProfileManager.PrintLong("YearsT:" + String.valueOf(YEARS_TOTAL) + "\nMonthsT:" + String.valueOf(MONTHS_TOTAL) + "\nWeeksT:" + String.valueOf(WEEKS_TOTAL) + "\nDaysT:" + String.valueOf(DAYS_TOTAL));

        // REPEAT_FREQUENCY * REPEAT_EVERY_N * REPEAT_TIMES
        Period event_period = new Period(YEARS_TOTAL, MONTHS_TOTAL, WEEKS_TOTAL, DAYS_TOTAL, 0, 0, 0, 0);

        //event_start  -  Always Given
        event_start = date;
        ProfileManager.PrintLong("event_start:" + event_start.toString(ProfileManager.simpleDateFormat));

        //event_end  -  RepeatUntil a date, a number of times, or forever
        event_end = event_start.plus(event_period);

        ProfileManager.PrintLong("event_end1:" + (event_end!= null ? event_end.toString(ProfileManager.simpleDateFormat) : "NULL"));

        switch(repeatUntil){
            case FOREVER: //Set event end to the timeFrame end
                event_end = end;
                break;
            case TIMES:
                break;
            case DATE:
                event_end = dateMin(repeatUntilDate, event_end);
                break;
        }
        ProfileManager.PrintLong("event_end2:" + (event_end!= null ? event_end.toString(ProfileManager.simpleDateFormat) : "NULL"));

        //event_lastOccurrence  -  The event_end may be farther along than the last time the event repeats. Subtract repeatEveryN-1
        if (event_end != null) {
            Period event_period_minus = new Period((YEARS != 0 ? (YEARS - 1) : 0), (MONTHS != 0 ? (MONTHS - 1) : 0), (WEEKS != 0 ? (WEEKS - 1) : 0), (DAYS != 0 ? (DAYS - 1) : 0), 0, 0, 0, 0);
            ProfileManager.PrintLong("event_period_minus:" + event_period_minus.toString());
            event_lastOccurrence = event_end.minus(event_period_minus);
        }


        ProfileManager.PrintLong("event_lastOccurrence:" + (event_lastOccurrence!= null ? event_lastOccurrence.toString(ProfileManager.simpleDateFormat) : "NULL"));


        //Add Occurrences
        event_period = new Period( YEARS, MONTHS, WEEKS, DAYS, 0,0,0,0 );
        event = event_start;
        //while (event.compareTo(event_lastOccurrence) <= 0){
        //    occurrences.add(event);

        //    event.plus(event_period);
        //}

        ProfileManager.PrintLong("Occurrences:" + occurrences.toString());


        switch(repeatFrequency){
            case NEVER:
                //occurrences.add(date);
                break;
            case WEEKLY:
                //event = event_start;
                //for (int i = 0; i < 7; i++){
                //    if (event.compareTo(event_start) >= 0 && event.compareTo(event_lastOccurrence) <= 0) { occurrences.add(event); }
                //    event = event.plusWeeks(repeatEveryN);
                //}
                break;
            default:
                break;
        }


        //Time Frame
        //timeFrame_start
        //timeFrame_start = start;
        //timeFrame_end
        //timeFrame_end = end;


        //Overlap timeframe



    */

    /*
        LocalDate lastDayOfEvent = null;
        LocalDate actualStartDay = null;
        LocalDate actualEndDay = null;
        LocalDate currentDay = null;

        if (repeatFrequency != Repeat.NEVER) {
            //Start null check
            if (start == null) { start = date; }
            //End null check
            if (end == null) { end = date; }
        }

        if (repeatFrequency == Repeat.NEVER) {
            if (date != null) {
                if (start != null && end != null && date.compareTo(start) >= 0 && date.compareTo(end) <= 0) { occurrences.add(date); }
                else if (start != null && end == null && date.compareTo(start) >= 0) { occurrences.add(date); }
                else if (start == null && end != null && date.compareTo(end) <= 0) { occurrences.add(date); }
                else if (start == null && end == null) { occurrences.add(date); }
                //else { return 0; } //Do nothing
            }
            //else { } //Do Nothing
        }
        else if (repeatFrequency == Repeat.DAILY) {
            //Start Day calculation (Find the first day after start that the event occurred)
            actualStartDay = dateMin(calcNextDay(date, start, repeatEveryN), end);

            //End Day calculation
            actualEndDay = dateMin(repeatUntilDate, end);

            //If the event repeats N times
            if (repeatUntil == RepeatUntil.TIMES) {
                lastDayOfEvent = date.plusDays((repeatUntilTimes * repeatEveryN) - repeatEveryN);

                //If the last day the event occurred was before the start date, return 0 occurrences
                if (lastDayOfEvent.compareTo(start) >= 0) {
                    //If the last day of the event falls within the range [start, end], adjust the end date to the last day of the event
                    if (lastDayOfEvent.compareTo(actualEndDay) <= 0 && lastDayOfEvent.compareTo(actualStartDay) >= 0) {
                        actualEndDay = lastDayOfEvent;
                    }
                }
            }

            //Occurrences calculation
            int days = (int) Math.ceil((double) (Days.daysBetween(actualStartDay, actualEndDay).getDays() + 1) / (double) repeatEveryN);
            currentDay = actualStartDay;
            for (int ii = 0; ii < days; ii++){
                occurrences.add(currentDay);
                currentDay = currentDay.plusDays(repeatEveryN);
            }

        } else if (repeatFrequency == Repeat.WEEKLY) {
            int weeks = 0;

            //Loop for each day of the week
            for (int i = 1; i < 8; i++) {
                if (repeatDayOfWeek[i - 1]) {
                    //Start occurs at least at date, and at most, end. Nearest dayOfWeek[i] is selected.
                    actualStartDay = dateMin(dateMax(start, date), end);
                    actualStartDay = actualStartDay.withDayOfWeek(i);

                    //End occurs at repeatUntilDate or end, whichever comes first. Nearest dayOfWeek[i] is selected.
                    actualEndDay = dateMin(repeatUntilDate, end);
                    actualEndDay = dateMin(calcPrevDayOfWeek(actualEndDay, i), actualEndDay);

                    //If date is within the range (infinity, end]
                    if (date.compareTo(actualEndDay) <= 0) {
                        //If the event repeats N times
                        if (repeatUntil == RepeatUntil.TIMES) {
                            lastDayOfEvent = calcNextDayOfWeek(date, i).plusWeeks( (repeatUntilTimes * repeatEveryN) - repeatEveryN );


                            //If the last day the event occurred was before the start date, return 0 occurrences
                            if (lastDayOfEvent.compareTo(start) >= 0) {
                                //If the last day of the event falls within the range [start, end], adjust the end date to the last day of the event
                                if (lastDayOfEvent.compareTo(actualEndDay) <= 0 && lastDayOfEvent.compareTo(actualStartDay) >= 0) {
                                    actualEndDay = lastDayOfEvent;
                                }
                            }
                        }

                        //ProfileManager.PrintLong("ActualStart:" + actualStartDay.toString(ProfileManager.simpleDateFormat));
                        //ProfileManager.PrintLong("ActualEnd:" + actualEndDay.toString(ProfileManager.simpleDateFormat));

                        //Add to occurrences
                        weeks = Weeks.weeksBetween(calcPrevDayOfWeek(actualStartDay, 1).minusDays(1), calcNextDayOfWeek(actualEndDay, 7).plusDays(1)).getWeeks();
                        weeks = (int)Math.ceil((double)weeks / repeatEveryN);
                        //ProfileManager.PrintLong("Weeks:" + String.valueOf(weeks));

                        currentDay = actualStartDay;
                        for (int ii = 0; ii < weeks; ii++){
                            if (currentDay.compareTo(date) >= 0 && currentDay.compareTo(actualEndDay) <= 0) { occurrences.add(currentDay); }
                            currentDay = currentDay.plusWeeks(repeatEveryN);
                        }
                    }
                }
            }

        } else if (repeatFrequency == Repeat.MONTHLY) {
            //Find first occurrence of dayOfMonth after date, clamp to end date
            actualStartDay = dateMin(calcNextDayOfMonth(date, repeatDayOfMonth), end);
            //ProfileManager.PrintLong("ActualStart1:" + actualStartDay.toString(ProfileManager.simpleDateFormat));

            //End occurs at repeatUntilDate or end, whichever comes first. Nearest dayOfWeek[i] is selected.
            actualEndDay = dateMin(repeatUntilDate, end);
            actualEndDay = dateMin(calcPrevDayOfMonth(actualEndDay, repeatDayOfMonth), actualEndDay);


            //If date is within the range (infinity, end]
            if (date.compareTo(actualEndDay) <= 0) {

                //If the event repeats N times
                if (repeatUntil == RepeatUntil.TIMES) {
                    lastDayOfEvent = date.plusMonths( (repeatUntilTimes * repeatEveryN) - repeatEveryN );
                    //ProfileManager.PrintLong( "LastDayOfEvent:" + lastDayOfEvent.toString(ProfileManager.simpleDateFormat) );

                    //If the last day the event occurred was before the start date, return 0 occurrences
                    if (lastDayOfEvent.compareTo(start) >= 0) {
                        //If the last day of the event falls within the range [start, end], adjust the end date to the last day of the event
                        if (lastDayOfEvent.compareTo(actualEndDay) <= 0 && lastDayOfEvent.compareTo(actualStartDay) >= 0) {
                            actualEndDay = lastDayOfEvent;
                        }
                    }
                    else{
                        return occurrences;
                    }

                }

                //Find the first month after start that the event occurs
                actualStartDay = calcNextMonth(actualStartDay, start, repeatEveryN, repeatDayOfMonth);
                //ProfileManager.PrintLong("ActualStart2:" + actualStartDay.toString(ProfileManager.simpleDateFormat));


                //ProfileManager.PrintLong("ActualEnd:" + actualEndDay.toString(ProfileManager.simpleDateFormat));

                //Add to occurrences
                int months = Months.monthsBetween(actualStartDay.dayOfMonth().withMinimumValue().minusDays(1), actualEndDay.dayOfMonth().withMaximumValue().plusDays(1)).getMonths();
                months = (int)Math.ceil((double)months / repeatEveryN);

                currentDay = actualStartDay;
                for (int ii = 0; ii < months; ii++){
                    occurrences.add(currentDay);
                    currentDay = currentDay.plusMonths(repeatEveryN);
                    currentDay = currentDay.withDayOfMonth(Math.min(currentDay.dayOfMonth().withMaximumValue().getDayOfMonth(), repeatDayOfMonth));
                }

                //ProfileManager.PrintLong("months:" + String.valueOf(months));
            }
        } else if (repeatFrequency == Repeat.YEARLY){

        }
        */
		
		
		
		/*
    LocalDate diff = new LocalDate();

                        long startDay = start.getTimeInMillis();
                        long endDay = end.getTimeInMillis();

                        long expenseBeginning = date.getTimeInMillis();
                        ProfileManager.PrintLong("expenseBeg:" + ProfileManager.simpleDateFormat.format(expenseBeginning));
                        long expenseRepeatUntil = (repeatUntilDate != null ? repeatUntilDate.getTimeInMillis() : 0);
                        ProfileManager.PrintLong("expenseRepUnt:" + ProfileManager.simpleDateFormat.format(expenseRepeatUntil));

                        diff.setTimeInMillis(startDay - expenseBeginning);

                        //int addDays = (int)( Math.ceil( (double)(diff.get(Calendar.DAY_OF_YEAR)) / repeatEveryN ) * repeatEveryN);
                        int k = ( repeatEveryN - ( (diff.get(LocalDate.DAY_OF_YEAR)) % repeatEveryN) );
                        int addDays = k * (k == repeatEveryN ? 0 : 1);
                        ProfileManager.PrintLong("K:" + String.valueOf(k));
                        ProfileManager.PrintLong("AddDays:" + String.valueOf(addDays));

                        diff.setTimeInMillis(startDay);
                        diff.add(LocalDate.DATE, addDays);

                        long actualStartDay = diff.getTimeInMillis();
                        ProfileManager.PrintLong("ActualStart:" + ProfileManager.simpleDateFormat.format(actualStartDay));

                        long actualEndDay = (repeatUntilDate != null ? Math.min(endDay, expenseRepeatUntil) : endDay);
                        ProfileManager.PrintLong("ActualEnd:" + ProfileManager.simpleDateFormat.format(actualEndDay));

                        diff.setTimeInMillis(actualEndDay - actualStartDay);
                        ProfileManager.PrintLong("Diff:" + ProfileManager.simpleDateFormat.format(diff.getTime()));

                        occurences = (int)Math.ceil( (diff.get(LocalDate.DAY_OF_YEAR)) / repeatEveryN );
                        ProfileManager.PrintLong("Occur:" + String.valueOf( occurences ) );



                        //LocalDate diff;

                //long startDay = start.getTimeInMillis();
                //long endDay = end.getTimeInMillis();

                //long expenseBeginning = date.getTimeInMillis();
                //ProfileManager.PrintLong("expenseBeg:" + ProfileManager.simpleDateFormat.format(expenseBeginning));
                //long expenseRepeatUntil = (repeatUntilDate != null ? repeatUntilDate.getTimeInMillis() : 0);
                //ProfileManager.PrintLong("expenseRepUnt:" + ProfileManager.simpleDateFormat.format(expenseRepeatUntil));

                //diff.setTimeInMillis(startDay - expenseBeginning);

                //int addDays = (int)( Math.ceil( (double)(diff.get(Calendar.DAY_OF_YEAR)) / repeatEveryN ) * repeatEveryN);
                //ProfileManager.PrintLong("DaysBtwn:" + String.valueOf(Days.daysBetween(date, start).getDays()));

                //int lastOccurrenceOfEventAfterDate;
                int daysBtwnDateandStart;
                int k;
                int firstOccurrenceOfEventAfterStart;

                LocalDate lastDayOfEvent = null;
                LocalDate actualStartDay = null;
                LocalDate actualEndDay = null;


                //Start Day calculation (Find the first day after start that the event occurred)
                daysBtwnDateandStart = Days.daysBetween(date, start).getDays();
                k = repeatEveryN - (daysBtwnDateandStart % repeatEveryN);
                firstOccurrenceOfEventAfterStart = k * (k == repeatEveryN ? 0 : 1);
                actualStartDay = start.plusDays(firstOccurrenceOfEventAfterStart);

                //End Day calculation
                actualEndDay = ProfileManager.dateMin(end, repeatUntilDate);

                ProfileManager.PrintLong( "DaysBtwnDateandStart:" + String.valueOf(daysBtwnDateandStart) );
                ProfileManager.PrintLong( "k:" + String.valueOf(k) );
                ProfileManager.PrintLong( "firstOccurrenceOfEventAfterStart:" + String.valueOf(firstOccurrenceOfEventAfterStart) );
                ProfileManager.PrintLong( "ActualStart:" + actualStartDay.toString(ProfileManager.simpleDateFormat) );
                ProfileManager.PrintLong( "ActualEnd:" + actualEndDay.toString(ProfileManager.simpleDateFormat) );

                //If the event repeats N times
                if ( repeatUntil == RepeatUntil.TIMES ){
                    lastDayOfEvent = date.plusDays((repeatUntilTimes * repeatEveryN) - repeatEveryN);
                    ProfileManager.PrintLong( "LastDayOfEvent:" + lastDayOfEvent.toString(ProfileManager.simpleDateFormat) );

                    //If the last day the event occurred was before the start date, return 0 occurrences
                    if ( lastDayOfEvent.compareTo(start) < 0 ){
                        ProfileManager.PrintLong("Occurrences=0; Event did not occur within range [start, end]");
                        return 0; //Return 0
                    }
                    else {
                        //If the last day of the event falls within the range [start, end], adjust the end date to the last day of the event
                        if ( lastDayOfEvent.compareTo(actualEndDay) <= 0 && lastDayOfEvent.compareTo(actualStartDay) >= 0 ){
                            actualEndDay = lastDayOfEvent;
                        }
                    }
                }

                //Occurrences calculation
                occurrences = (int)Math.ceil( (double)(Days.daysBetween(actualStartDay, actualEndDay).getDays()+1) / (double)repeatEveryN );
                ProfileManager.PrintLong( "Occurrences:" + String.valueOf( occurrences ) );


                //(daysBtwnDateandStart / repeatEveryN) < repeatUntilTimes;

                //date.plusDays(this) == last day of last section of length repeatEveryN. If this - repeatEveryN < start, then the event ended before the timeFrame
                //lastOccurrenceOfEventAfterDate = (repeatUntilTimes != 0 ? (repeatUntilTimes * repeatEveryN) - repeatEveryN : 0);
                //ProfileManager.PrintLong("lastOccurenceOfEventAfterDate:" + String.valueOf(lastOccurrenceOfEventAfterDate));

                //Defaults to date if no repeatUntilTimes is set
                //lastDayOfEvent = date.plusDays(lastOccurrenceOfEventAfterDate);
                //ProfileManager.PrintLong("lastDayOfEvent:" + lastDayOfEvent.toString(ProfileManager.simpleDateFormat));


                //daysBtwnDateandStart = (Days.daysBetween(date, start).getDays());
                //k = repeatEveryN - ( daysBtwnDateandStart % repeatEveryN );
                //firstOccurrenceOfEventAfterStart = k * (k == repeatEveryN ? 0 : 1);
                //ProfileManager.PrintLong("DaysBtwnDateandStart:" + String.valueOf(daysBtwnDateandStart));
                //ProfileManager.PrintLong("k:" + String.valueOf(k));
                //ProfileManager.PrintLong("firstOccurrenceOfEventAfterStart:" + String.valueOf(firstOccurrenceOfEventAfterStart));


                //ProfileManager.PrintLong("K:" + String.valueOf(k));
                //ProfileManager.PrintLong("AddDays:" + String.valueOf(addDays));

                //diff.setTimeInMillis(startDay);
                //diff.add(LocalDate.DATE, addDays);

                //actualStartDay = start.plusDays(firstOccurrenceOfEventAfterStart);
                //ProfileManager.PrintLong("ActualStart:" + actualStartDay.toString(ProfileManager.simpleDateFormat));

                //actualEndDay = ProfileManager.dateMin(ProfileManager.dateMin(repeatUntilDate, end), lastDayOfEvent); //(repeatUntilDate != null ? (end.compareTo(repeatUntilDate) <= 0 ? end : repeatUntilDate ) : end);
                //Long actualEndDay = (repeatUntilDate != null ? Math.min(endDay, expenseRepeatUntil) : endDay);
                //ProfileManager.PrintLong("ActualEnd:" + actualEndDay.toString(ProfileManager.simpleDateFormat));

                //diff.setTimeInMillis(actualEndDay - actualStartDay);
                //ProfileManager.PrintLong("Diff:" + ProfileManager.simpleDateFormat.format(diff.getTime()));
                //ProfileManager.PrintLong("Days:" + String.valueOf(Days.daysBetween(actualStartDay, actualEndDay).getDays()+1));
                //occurrences = (int)Math.ceil( (double)(Days.daysBetween(actualStartDay, actualEndDay).getDays()+1) / (double)repeatEveryN );
                //ProfileManager.PrintLong("Occur:" + String.valueOf( occurrences ) );
     */
	 
	 
	 
	 
	 
	     /*
    public static LocalDate calcNextDay(LocalDate date, LocalDate start, int repeat) {
        if (date.isBefore(start)) { //Only calculate if date comes before start
            int daysBtwnDateandStart = Days.daysBetween(dateMin(date, start), dateMax(date, start)).getDays();
            //ProfileManager.PrintLong("daysBtwn:" + String.valueOf(daysBtwnDateandStart));
            int k = repeat - (daysBtwnDateandStart % repeat);
            //ProfileManager.PrintLong("k:" + String.valueOf(k));
            int firstOccurrenceOfEventAfterStart = k * (k == repeat ? 0 : 1);
            //ProfileManager.PrintLong("fiorstOcc:" + String.valueOf(firstOccurrenceOfEventAfterStart));
            return start.plusDays(firstOccurrenceOfEventAfterStart);
        }
        else{
            return date;
        }
    }
    public static LocalDate calcNextMonth(LocalDate date, LocalDate start, int repeat, int repeatDayOfMonth) {
        if (date.isBefore(start)) { //Only calculate if date comes before start
            start = calcNextDayOfMonth(start, repeatDayOfMonth);

            int monthsBtwnDateandStart = Months.monthsBetween(dateMin(date, start), dateMax(date, start)).getMonths();
            //ProfileManager.PrintLong("mnthsBtwn:" + String.valueOf(monthsBtwnDateandStart));
            int k = repeat - (monthsBtwnDateandStart % repeat);
            //ProfileManager.PrintLong("k:" + String.valueOf(k));
            int firstOccurrenceOfEventAfterStart = k * (k == repeat ? 0 : 1);
            //ProfileManager.PrintLong("fiorstOcc:" + String.valueOf(firstOccurrenceOfEventAfterStart));
            return start.plusMonths(firstOccurrenceOfEventAfterStart).dayOfMonth().withMinimumValue();
        }
        else {
            return date;
        }
    }
    */
