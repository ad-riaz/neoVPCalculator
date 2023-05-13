package io.github.adriaz.neovpcalculator.model;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import io.github.adriaz.neovpcalculator.exception.NoHolidaysException;
import io.github.adriaz.neovpcalculator.exception.NoSuchYearException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.time.LocalDate;

public class Calendar {
    private static final JSONParser parser = new JSONParser();
    private final JSONObject calendar;

    public Calendar(String calendarPath) {
        try (Reader fileReader = new FileReader(calendarPath)) {
            calendar = (JSONObject) parser.parse(fileReader);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayList<LocalDate> getHolidays(String year) throws NoSuchYearException, NoHolidaysException {
        ArrayList<LocalDate> holidays = new ArrayList<>();
        JSONObject calPerYear = (JSONObject) calendar.get(year);

        if (calPerYear == null) {
            throw new NoSuchYearException("Год " + year + " не найден в существующем производственном календаре");
        }

        JSONArray holidayCal = (JSONArray) calPerYear.get("holidays");

        if (holidayCal == null) {
            throw new NoHolidaysException("В производственном календаре не найдено праздников за " + year + " год");
        }

        Iterator<String> iterator = holidayCal.iterator();
        while (iterator.hasNext()) {
            holidays.add(LocalDate.parse(iterator.next()));
        }

        return holidays;
    }

    public boolean isHoliday(LocalDate date) throws NoSuchYearException, NoHolidaysException {
        String year = Integer.toString(date.getYear());
        ArrayList<LocalDate> holidays = getHolidays(year);

        for (LocalDate holiday : holidays) {
            if (date.equals(holiday)) return true;
        }

        return false;
    }
}
