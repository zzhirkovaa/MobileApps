package ru.mirea.zhirkova.simplefragmentapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    Fragment fragment1, fragment2;
    FragmentManager fragmentManager;
    private View fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment1 = new FirstFragment();
        fragment2 = new SecondFragment();
        fragmentContainer = findViewById(R.id.fragmentContainer);

        if (fragmentContainer != null && savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment1)
                    .commit();
        }
    }

    public void onClick(View view) {
        if (fragmentContainer == null) {
            return;
        }

        fragmentManager = getSupportFragmentManager();

        int id = view.getId();
        if (id == R.id.btnFirstFragment) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment1)
                    .commit();
        } else if (id == R.id.btnSecondFragment) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment2)
                    .commit();
        }
    }
}
