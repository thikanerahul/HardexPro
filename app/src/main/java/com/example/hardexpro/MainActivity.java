package com.example.hardexpro;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.hardexpro.databinding.ActivityMainBinding;
import com.example.hardexpro.ui.base.BaseActivity;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    private com.example.hardexpro.ui.base.MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.bottomNav, navController);
        }
    }

    @Override
    protected void initViewModel() {
        viewModel = new androidx.lifecycle.ViewModelProvider(this)
                .get(com.example.hardexpro.ui.base.MainViewModel.class);
    }
}