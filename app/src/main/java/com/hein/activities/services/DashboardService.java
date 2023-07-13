package com.example.a2handee.activities.services;

import com.hein.entity.Booking;
import com.github.mikephil.charting.data.BarEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DashboardService {

    public static Map<String, Integer> getIncome(List<Booking> listBooking) {
        double sumIncome = listBooking.stream().mapToDouble(Booking::getTotalPrice).sum();

        Map<String, Integer> resultsIncome = new HashMap<>();
        resultsIncome.put("total", (int) sumIncome);
        resultsIncome.put("average", (int) sumIncome / listBooking.size());

        return resultsIncome;

    }

    public static List<String> getNotDuplicatedListId(List<Booking> bookingList) {
        Map<String, Integer> bookingMap = new HashMap<>();

        bookingList.forEach((booking -> {
            if(!bookingMap.containsKey(booking.getProductId())) {
                bookingMap.put(booking.getProductId(), 0);
            }
        }));

        List<String> listProductId = new ArrayList<>(bookingMap.keySet());


        return listProductId;

    }

    public static List<BarEntry> getListBarEntries(List<Booking> bookingList) throws ParseException {
        List<BarEntry> listBarEntries = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        Map<Integer, Integer> listData = new HashMap<>();

        for (Booking booking: bookingList) {
            Date date = format.parse(booking.getTimestamp());

            if(listData.containsKey(date.getDate())) {
                listData.put(date.getDate(), (int) listData.get(date.getDate()) + (int) booking.getTotalPrice());
            } else {
                listData.put(date.getDate(), (int) booking.getTotalPrice());
            }
        }

        List<Integer> listId = new ArrayList<>(listData.keySet());


        for(Integer id : listId) {
            listBarEntries.add(new BarEntry(id, listData.get(id)));
        }

        return listBarEntries;
    }
}
