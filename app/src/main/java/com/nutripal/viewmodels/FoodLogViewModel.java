package com.nutripal.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.nutripal.models.FoodLog;
import com.nutripal.repositories.FoodLogRepository;
import java.util.Calendar;
import java.util.List;

public class FoodLogViewModel extends AndroidViewModel {

    private final FoodLogRepository repository;
    private final MutableLiveData<Calendar> selectedDate = new MutableLiveData<>();
    private final LiveData<List<FoodLog>> logsForSelectedDate;

    public FoodLogViewModel(@NonNull Application application) {
        super(application);
        repository = new FoodLogRepository(application);
        selectedDate.setValue(Calendar.getInstance());

        logsForSelectedDate = Transformations.switchMap(selectedDate, date -> {
            Calendar startCal = (Calendar) date.clone();
            startCal.set(Calendar.HOUR_OF_DAY, 0);
            startCal.set(Calendar.MINUTE, 0);
            startCal.set(Calendar.SECOND, 0);
            long startOfDay = startCal.getTimeInMillis();

            Calendar endCal = (Calendar) date.clone();
            endCal.add(Calendar.DAY_OF_YEAR, 1);
            endCal.set(Calendar.HOUR_OF_DAY, 0);
            endCal.set(Calendar.MINUTE, 0);
            endCal.set(Calendar.SECOND, 0);
            long endOfDay = endCal.getTimeInMillis() - 1;

            return repository.getLogsForDate(startOfDay, endOfDay);
        });
    }

    public LiveData<List<FoodLog>> getLogsForSelectedDate() { return logsForSelectedDate; }
    public LiveData<Calendar> getSelectedDate() { return selectedDate; }

    public void nextDay() {
        Calendar newDate = (Calendar) selectedDate.getValue().clone();
        newDate.add(Calendar.DAY_OF_YEAR, 1);
        selectedDate.setValue(newDate);
    }

    public void previousDay() {
        Calendar newDate = (Calendar) selectedDate.getValue().clone();
        newDate.add(Calendar.DAY_OF_YEAR, -1);
        selectedDate.setValue(newDate);
    }

    public void insert(FoodLog foodLog) { repository.insert(foodLog); }
    public void delete(FoodLog foodLog) { repository.delete(foodLog); }
}
