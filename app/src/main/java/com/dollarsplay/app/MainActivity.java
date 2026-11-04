package com.dollarsplay.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends Activity {

    SharedPreferences prefs;
    TextView balanceText, welcomeText;
    double balance = 0;
    
    // Pages
    View splashPage, loginPage, homePage, spinPage, checkinPage, withdrawPage, tasksPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        prefs = getSharedPreferences("DollarsPlay", MODE_PRIVATE);
        balance = prefs.getFloat("balance", 0f);
        
        // Find Pages
        splashPage = findViewById(R.id.splashPage);
        loginPage = findViewById(R.id.loginPage);
        homePage = findViewById(R.id.homePage);
        spinPage = findViewById(R.id.spinPage);
        checkinPage = findViewById(R.id.checkinPage);
        withdrawPage = findViewById(R.id.withdrawPage);
        tasksPage = findViewById(R.id.tasksPage);
        
        balanceText = findViewById(R.id.balanceText);
        welcomeText = findViewById(R.id.welcomeText);
        
        setupLogin();
        setupHome();
        setupSpin();
        setupCheckin();
        setupWithdraw();
        setupTasks();
        
        // Show Splash then decide
        showPage(splashPage);
        new Handler().postDelayed(() -> {
            if (prefs.getBoolean("isLoggedIn", false)) {
                showHome();
            } else {
                showPage(loginPage);
            }
        }, 2500);
    }

    void showPage(View page) {
        splashPage.setVisibility(View.GONE);
        loginPage.setVisibility(View.GONE);
        homePage.setVisibility(View.GONE);
        spinPage.setVisibility(View.GONE);
        checkinPage.setVisibility(View.GONE);
        withdrawPage.setVisibility(View.GONE);
        tasksPage.setVisibility(View.GONE);
        page.setVisibility(View.VISIBLE);
        if (page == homePage) updateBalance();
    }

    void updateBalance() {
        balanceText.setText("$" + String.format("%.2f", balance));
        welcomeText.setText("Hi, " + prefs.getString("userName", "User") + " 👋");
    }

    void addBalance(double amount) {
        balance += amount;
        prefs.edit().putFloat("balance", (float) balance).apply();
        updateBalance();
        Toast.makeText(this, "+ $" + amount + " Added!", Toast.LENGTH_SHORT).show();
    }

    // LOGIN
    void setupLogin() {
        EditText nameInput = findViewById(R.id.nameInput);
        EditText emailInput = findViewById(R.id.emailInput);
        EditText passInput = findViewById(R.id.passInput);
        Button loginBtn = findViewById(R.id.loginBtn);
        Button googleBtn = findViewById(R.id.googleBtn);

        loginBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String pass = passInput.getText().toString().trim();
            if (name.isEmpty() || email.isEmpty() || pass.length() < 6) {
                Toast.makeText(this, "Enter Name, Email & 6+ char Password", Toast.LENGTH_SHORT).show();
                return;
            }
            prefs.edit().putBoolean("isLoggedIn", true).putString("userName", name).putString("userEmail", email).apply();
            showHome();
        });
        
        googleBtn.setOnClickListener(v -> {
            prefs.edit().putBoolean("isLoggedIn", true).putString("userName", "Google User").putString("userEmail", "user@gmail.com").apply();
            Toast.makeText(this, "Google Login Successful!", Toast.LENGTH_SHORT).show();
            showHome();
        });
    }

    void showHome() {
        showPage(homePage);
    }

    // HOME
    void setupHome() {
        findViewById(R.id.btnToTasks).setOnClickListener(v -> showPage(tasksPage));
        findViewById(R.id.btnToSpin).setOnClickListener(v -> showPage(spinPage));
        findViewById(R.id.btnToCheckin).setOnClickListener(v -> {
            int streak = prefs.getInt("streak", 0);
            TextView streakText = findViewById(R.id.streakText);
            streakText.setText("Streak: " + streak + " Days");
            showPage(checkinPage);
        });
        findViewById(R.id.btnToWithdraw).setOnClickListener(v -> showPage(withdrawPage));
        findViewById(R.id.tapEarnBtn).setOnClickListener(v -> addBalance(0.10));
    }

    // SPIN WHEEL
    void setupSpin() {
        Button spinBtn = findViewById(R.id.spinBtn);
        TextView spinResult = findViewById(R.id.spinResult);
        findViewById(R.id.backFromSpin).setOnClickListener(v -> showHome());
        
        spinBtn.setOnClickListener(v -> {
            String today = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
            String lastSpin = prefs.getString("lastSpin", "");
            if (today.equals(lastSpin)) {
                Toast.makeText(this, "Already spun today! Come tomorrow", Toast.LENGTH_SHORT).show();
                return;
            }
            double rewards[] = {0.10, 0.20, 0.50, 0.05, 1.00, 0.25};
            double reward = rewards[new Random().nextInt(rewards.length)];
            spinResult.setText("You Won: $" + reward);
            addBalance(reward);
            prefs.edit().putString("lastSpin", today).apply();
        });
    }

    // DAILY CHECK-IN
    void setupCheckin() {
        Button checkinBtn = findViewById(R.id.checkinBtn);
        findViewById(R.id.backFromCheckin).setOnClickListener(v -> showHome());
        
        checkinBtn.setOnClickListener(v -> {
            String today = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
            String lastCheckin = prefs.getString("lastCheckin", "");
            if (today.equals(lastCheckin)) {
                Toast.makeText(this, "Already checked today!", Toast.LENGTH_SHORT).show();
                return;
            }
            int streak = prefs.getInt("streak", 0) + 1;
            double reward = 0.10 * streak;
            if (reward > 1.0) reward = 1.0;
            addBalance(reward);
            prefs.edit().putString("lastCheckin", today).putInt("streak", streak).apply();
            TextView streakText = findViewById(R.id.streakText);
            streakText.setText("Streak: " + streak + " Days");
            Toast.makeText(this, "Day " + streak + " Check-in Done!", Toast.LENGTH_SHORT).show();
        });
    }

    // WITHDRAWAL
    void setupWithdraw() {
        Spinner methodSpinner = findViewById(R.id.methodSpinner);
        EditText accountInput = findViewById(R.id.accountInput);
        EditText amountInput = findViewById(R.id.amountInput);
        Button withdrawBtn = findViewById(R.id.withdrawBtn);
        findViewById(R.id.backFromWithdraw).setOnClickListener(v -> showHome());
        
        String[] methods = {"Easypaisa", "JazzCash", "Bank Transfer", "Payoneer"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, methods);
        methodSpinner.setAdapter(adapter);
        
        withdrawBtn.setOnClickListener(v -> {
            String amountStr = amountInput.getText().toString();
            String account = accountInput.getText().toString();
            if (amountStr.isEmpty() || account.isEmpty()) {
                Toast.makeText(this, "Enter Amount & Account", Toast.LENGTH_SHORT).show();
                return;
            }
            double amount = Double.parseDouble(amountStr);
            if (amount < 10) {
                Toast.makeText(this, "Minimum withdrawal is $10", Toast.LENGTH_SHORT).show();
                return;
            }
            if (amount > balance) {
                Toast.makeText(this, "Not enough balance! Balance: $" + balance, Toast.LENGTH_SHORT).show();
                return;
            }
            balance -= amount;
            prefs.edit().putFloat("balance", (float) balance).apply();
            updateBalance();
            Toast.makeText(this, "Withdrawal Request Sent! $" + amount + " via " + methodSpinner.getSelectedItem(), Toast.LENGTH_LONG).show();
            showHome();
        });
    }

    // TASKS
    void setupTasks() {
        findViewById(R.id.backFromTasks).setOnClickListener(v -> showHome());
        findViewById(R.id.taskVideoBtn).setOnClickListener(v -> {
            addBalance(0.15);
            Toast.makeText(this, "Video Task Completed!", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.taskGameBtn).setOnClickListener(v -> {
            addBalance(0.20);
            Toast.makeText(this, "Game Task Completed! +$0.20", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.taskInviteBtn).setOnClickListener(v -> {
            Toast.makeText(this, "Invite Link Copied! Earn $1 per friend", Toast.LENGTH_SHORT).show();
        });
    }
    
    @Override
    public void onBackPressed() {
        if (homePage.getVisibility() == View.VISIBLE) {
            super.onBackPressed();
        } else {
            showHome();
        }
    }
}
