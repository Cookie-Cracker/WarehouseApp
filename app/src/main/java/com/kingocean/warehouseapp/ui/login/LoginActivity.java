package com.kingocean.warehouseapp.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.kingocean.warehouseapp.R;
import com.kingocean.warehouseapp.activities.AppsActivity;
import com.kingocean.warehouseapp.databinding.ActivityLoginBinding;
import com.kingocean.warehouseapp.utils.TastyToasty;

public class LoginActivity extends AppCompatActivity {

    //// ------------------------------------------------------------------------------------------------------------------------------
    public static Context contextOfApplication;
    //// ------------------------------------------------------------------------------------------------------------------------------

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        //// ----------------------------------------------------------------------------------------------------------------------------
        contextOfApplication = getApplicationContext();
        //// ----------------------------------------------------------------------------------------------------------------------------

        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);

        final EditText usernameEditText = binding.etUsername;
        final EditText passwordEditText = binding.etPassword;
        final Button loginButton = binding.buttonSignin;

        // ------------------------------------------------------------------------------------------------------------------------------
        // Get the version from build.gradle(:app)
        // ------------------------------------------------------------------------------------------------------------------------------
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
            //Toast.makeText(this, "PackageName = " + info.packageName + "\nVersionCode = " + info.versionCode + "\nVersionName = " + info.versionName + "\nPermissions = " + info.permissions, Toast.LENGTH_SHORT).show();
            TextView textViewAppVersion = (TextView) findViewById(R.id.textViewAppVersion);
            textViewAppVersion.setText("v." + info.versionCode + "." + info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // ------------------------------------------------------------------------------------------------------------------------------

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                ////loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                ////
                Context applicationContext = LoginActivity.getContextOfApplication();
                ////

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(applicationContext, usernameEditText.getText().toString(), passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(v -> {

            ////loadingProgressBar.setVisibility(View.VISIBLE);

            ////
            Context applicationContext = LoginActivity.getContextOfApplication();
            ////

            if (!usernameEditText.getText().toString().equalsIgnoreCase("") && !passwordEditText.getText().toString().equalsIgnoreCase("")) {

                loginViewModel.login(applicationContext, usernameEditText.getText().toString(), passwordEditText.getText().toString());

                SharedPreferences preferences = getSharedPreferences("credentials", MODE_PRIVATE);
                String id = preferences.getString("id", "");
                if (id.equalsIgnoreCase("")) {
                    TastyToasty.orange(getApplicationContext(), "Invalid Credentials.", R.drawable.ic_error).show();
                    goLogin();
                } else {
                    goScan();
                }

            } else {
                TastyToasty.orange(getApplicationContext(), "Please, enter credentials!", R.drawable.ic_error).show();

            }

        });

    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        TastyToasty.green(getApplicationContext(), welcome, R.drawable.ic_user).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        TastyToasty.green(getApplicationContext(), errorString.toString(), R.drawable.ic_error).show();

    }

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }

    public void goScan() {

        //Intent intent = new Intent(this, ScanActivity.class);

        //Intent intent;
        //try {
        //    ApplicationInfo ai = this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
        //    Object value = (Object)ai.metaData.get("application");
        //    if (value.equals("stuffing")) {
        //        intent = new Intent(this, StuffingActivity.class);
        //    } else {
        //        intent = new Intent(this, ScanActivity.class);
        //    }
        //} catch (PackageManager.NameNotFoundException e) {
        //    return;
        //}

        Intent intent;
        intent = new Intent(this, AppsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }

    public void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }

}
