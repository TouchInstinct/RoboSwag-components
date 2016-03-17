package ru.touchin.roboswag.components.calendar;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.touchin.roboswag.core.log.Lc;

/**
 * Created by Ilia Kurtov on 17.03.2016.
 */
public final class CalendarUtils {

    public static final int DAYS_IN_WEEK = 7;

    @Nullable
    public static CalendarItem find(@Nullable final List<CalendarItem> calendarItems, final long position) {
        if (calendarItems != null) {
            int low = 0;
            int high = calendarItems.size() - 1;
            while (true) {
                final int mid = (low + high) / 2;
                if (position < calendarItems.get(mid).getStartRange()) {
                    if (mid == 0 || position > calendarItems.get(mid - 1).getEndRange()) {
                        Lc.assertion("CalendarAdapter cannot find item with that position");
                        break;
                    }
                    high = mid - 1;
                } else if (position > calendarItems.get(mid).getEndRange()) {
                    if (mid == calendarItems.size() || position < calendarItems.get(mid + 1).getStartRange()) {
                        Lc.assertion("CalendarAdapter cannot find item with that position");
                        break;
                    }
                    low = mid + 1;
                } else {
                    return calendarItems.get(mid);
                }
            }
        } else {
            Lc.assertion("Calendar Items list is empty");
        }
        return null;
    }

    @Nullable
    @SuppressWarnings("checkstyle:MethodLength")
    public static Integer findPositionByDate(@Nullable final List<CalendarItem> calendarItems, final long date) {
        if (calendarItems == null || calendarItems.isEmpty()) {
            Lc.assertion("Calendar Items List is null");
            return null;
        }

        int low = 0;
        int high = calendarItems.size() - 1;
        int addition = 0;
        float count = 0;
        while (true) {
            final int mid = (low + high) / 2 + addition;
            if (calendarItems.get(mid) instanceof CalendarDayItem) {
                if (date < ((CalendarDayItem) calendarItems.get(mid)).getDateOfFirstDay()) {
                    if (mid == 0) {
                        Lc.assertion("Selected date smaller then min date in calendar");
                        break;
                    }
                    high = mid - 1;
                } else {
                    final long endDate = ((CalendarDayItem) calendarItems.get(mid)).getDateOfFirstDay()
                            + calendarItems.get(mid).getEndRange() - calendarItems.get(mid).getStartRange();
                    if (date > endDate) {
                        if (mid == calendarItems.size()) {
                            Lc.assertion("Selected date bigger then max date in calendar");
                            break;
                        }
                        low = mid + 1;
                    } else {
                        return (int) (calendarItems.get(mid).getStartRange()
                                + date - ((CalendarDayItem) calendarItems.get(mid)).getDateOfFirstDay());
                    }
                }
                count = 0;
                addition = 0;
            } else {
                count++;
                addition = ((int) Math.ceil(count / 2)) * ((int) (StrictMath.pow(-1, (count - 1))));
            }
        }
        return null;
    }

    @Nullable
    @SuppressWarnings("checkstyle:MethodLength")
    public static List<CalendarItem> fillRanges(@NonNull final Calendar startDate, @NonNull final Calendar endDate) {
        final Calendar cleanStartDate = getCleanDate(startDate);
        final Calendar cleanEndDate = getCleanDate(endDate);

        final List<CalendarItem> calendarItems = new ArrayList<>();

        final Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(cleanStartDate.getTime());

        calendarItems.add(new CalendarHeaderItem(calendar.get(Calendar.MONTH), 0, 0));
        int shift = 1;

        final int totalDaysCount = (int) ((cleanEndDate.getTimeInMillis() - cleanStartDate.getTimeInMillis()) / CalendarAdapter.ONE_DAY_LENGTH + 1);
        long firstRangeDate = calendar.getTimeInMillis() / CalendarAdapter.ONE_DAY_LENGTH + 1;
        int firstRange = calendar.get(Calendar.DAY_OF_MONTH) - 1;
        int daysEnded = 0;

        shift += getFirstDateStart(cleanStartDate);
        if (shift > 1) {
            calendarItems.add(new CalendarEmptyItem(1, shift - 1));
        }

        while (true) {
            final int daysInCurrentMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            if ((daysEnded + (daysInCurrentMonth - firstRange)) <= totalDaysCount) {
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                calendar.setTime(new Date(calendar.getTimeInMillis() + CalendarAdapter.ONE_DAY_LENGTH));

                calendarItems.add(new CalendarDayItem(firstRangeDate, firstRange + 1,
                        shift + daysEnded, shift + daysEnded + (daysInCurrentMonth - firstRange) - 1));
                daysEnded += daysInCurrentMonth - firstRange;
                if (daysEnded == totalDaysCount) {
                    break;
                }
                firstRangeDate = calendar.getTimeInMillis() / CalendarAdapter.ONE_DAY_LENGTH + 1;
                firstRange = 0;

                final int firstDayInWeek = getFirstDateStart(calendar);

                if (firstDayInWeek != 0) {
                    calendarItems.add(new CalendarEmptyItem(shift + daysEnded, shift + daysEnded + (DAYS_IN_WEEK - firstDayInWeek - 1)));
                    shift += (DAYS_IN_WEEK - firstDayInWeek);
                }

                calendarItems.add(new CalendarHeaderItem(calendar.get(Calendar.MONTH), shift + daysEnded, shift + daysEnded));
                shift += 1;

                if (firstDayInWeek != 0) {
                    calendarItems.add(new CalendarEmptyItem(shift + daysEnded, shift + daysEnded + firstDayInWeek - 1));
                    shift += firstDayInWeek;
                }

            } else {
                calendarItems.add(new CalendarDayItem(firstRangeDate, firstRange + 1, shift + daysEnded, shift + totalDaysCount));
                break;
            }
        }

        return calendarItems;
    }

    private static int getFirstDateStart(@NonNull final Calendar calendar) {
        int firstDateStart = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        if (firstDateStart == -1) {
            firstDateStart += DAYS_IN_WEEK;
        }
        return firstDateStart;
    }

    @NonNull
    private static Calendar getCleanDate(@NonNull final Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    private CalendarUtils() {
    }

}
