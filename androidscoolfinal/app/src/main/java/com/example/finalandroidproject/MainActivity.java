package com.example.finalandroidproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private FirebaseAuth auth;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            View headerView = navigationView.getHeaderView(0);

            TextView tvUsername = headerView.findViewById(R.id.tv_username);
            TextView tvEmail = headerView.findViewById(R.id.tv_email);

            if (tvEmail != null) {
                String email = user.getEmail();
                tvEmail.setText(email != null && !email.isEmpty() ? email : "אימייל לא זמין");
            }

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference userReference = firebaseDatabase.getReference()
                    .child("Users")
                    .child(user.getUid());

            // Fetch and set username
            userReference.child("name").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String displayName = task.getResult().getValue(String.class);
                    if (tvUsername != null) {
                        tvUsername.setText(displayName != null && !displayName.isEmpty() ? displayName : "שם משתמש לא זמין");
                    }
                } else {
                    Log.e("MainActivity", "Failed to fetch username: " + task.getException().getMessage());
                    if (tvUsername != null) {
                        tvUsername.setText("שם משתמש לא זמין");
                    }
                }
            });

            // Check if the user is admin
            userReference.child("admin").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Boolean isAdmin = task.getResult().getValue(Boolean.class);
                    if (isAdmin != null && isAdmin) {
                        Menu menu = navigationView.getMenu();
                        MenuItem adminItem = menu.findItem(R.id.nav_admin);
                        if (adminItem != null) {
                            adminItem.setVisible(true); // Show admin menu item
                        }
                    }
                } else {
                    Log.e("MainActivity", "Failed to fetch admin status: " + task.getException().getMessage());
                }
            });
        } else {
            Log.e("MainActivity", "No user is logged in.");
        }

        // Default fragment to load
        loadFragment(new MainFragment());

        // Handle navigation item selection
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                loadFragment(new MainFragment());
            } else if (id == R.id.nav_progress) {
                loadFragment(new ProgressFragment());
            } else if (id == R.id.nav_summaries) {
                loadFragment(new SummariesFragment());
            } else if (id == R.id.nav_videos) {
                loadFragment(new VideosFragment());
            } else if (id == R.id.nav_ambulance) {
                loadFragment(new AmbulanceFragment());
            } else if (id == R.id.nav_glossary) {
                loadFragment(new GlossaryFragment());
            } else if (id == R.id.nav_questions) {
                loadFragment(new QuestionsFragment());
            } else if (id == R.id.nav_tests) {
                loadFragment(new TestFragment());
            } else if (id == R.id.nav_mega_codes) {
                loadFragment(new MegaCodesFragment());
            } else if (id == R.id.nav_settings) {
                loadFragment(new SettingsFragment());
            } else if (id == R.id.nav_admin) {
                loadFragment(new AdminFragment()); // Load admin fragment
            } else if (id == R.id.nav_logout) {
                new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                        .setTitle("התנתקות")
                        .setMessage("האם אתה בטוח שברצונך להתנתק?")
                        .setPositiveButton("כן", (dialog, which) -> {
                            auth.signOut();
                            Intent intent = new Intent(MainActivity.this, OpenActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("ביטול", (dialog, which) -> dialog.dismiss())
                        .show();
                return true;
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
