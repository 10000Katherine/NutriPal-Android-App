package com.nutripal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nutripal.R;
import com.nutripal.adapters.AchievementAdapter;
import com.nutripal.utils.PreferenceManager;
import com.nutripal.viewmodels.AchievementViewModel; // We will create this ViewModel next

public class AchievementsFragment extends Fragment {

    private AchievementViewModel viewModel;
    private AchievementAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // We will create the ViewModel in our next step
        viewModel = new ViewModelProvider(this).get(AchievementViewModel.class);
        return inflater.inflate(R.layout.fragment_achievements, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_achievements_grid);
        adapter = new AchievementAdapter();
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);

        String userEmail = new PreferenceManager(requireContext()).getLoggedInUserEmail();

        // Observe all possible achievements
        viewModel.getAllAchievements().observe(getViewLifecycleOwner(), allAchievements -> {
            // Also observe earned IDs to pass both to the adapter
            if (userEmail != null && !userEmail.isEmpty()) {
                viewModel.getEarnedAchievementIds(userEmail).observe(getViewLifecycleOwner(), earnedIds -> {
                    adapter.setDisplayModeAll(allAchievements, earnedIds);
                });
            }
        });
    }
}