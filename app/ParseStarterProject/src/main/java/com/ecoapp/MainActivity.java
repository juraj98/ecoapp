/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.ecoapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Arrays;


public class MainActivity extends ActionBarActivity {


    boolean register = true;
    boolean complete = true;

    TextView name;
    TextView surname;
    TextView username;
    TextView mail;
    TextView pass;
    TextView confPass;

    TextView switchBtn;

    TextView usernameMsg;
    TextView passMsg;
    TextView mailMsg;
    TextView confPassMsg;
    TextView nameMsg;
    TextView surnameMsg;

    boolean validData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ParseUser.getCurrentUser() != null) {
            Intent i = new Intent(getApplicationContext(), EcoWall.class);
            startActivity(i);
        }

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        name = (TextView) findViewById(R.id.nameText);
        surname = (TextView) findViewById(R.id.surnameText);
        username = (TextView) findViewById(R.id.usernameText);
        mail = (TextView) findViewById(R.id.mailText);
        pass = (TextView) findViewById(R.id.passText);
        confPass = (TextView) findViewById(R.id.confPassText);
        switchBtn = (TextView) findViewById(R.id.switchBtn);

        mailMsg = (TextView) findViewById(R.id.mailMsg);
        usernameMsg = (TextView) findViewById(R.id.usernameMsg);
        passMsg = (TextView) findViewById(R.id.passMsg);
        confPassMsg = (TextView) findViewById(R.id.confPassMsg);
        nameMsg = (TextView) findViewById(R.id.nameMsg);
        surnameMsg = (TextView) findViewById(R.id.surnameMsg);


        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Button btn = (Button) findViewById(R.id.btn);

                final GridLayout menu = (GridLayout) findViewById(R.id.menu);

                if (complete) {
                    complete = false;
                    if (register) {
                        register = false;

                        username.setNextFocusDownId(R.id.passText);

                        nameMsg.animate().alpha(0f).setDuration(250).start();
                        confPassMsg.animate().alpha(0f).setDuration(250).start();
                        surnameMsg.animate().alpha(0f).setDuration(250).start();
                        mailMsg.animate().alpha(0f).setDuration(250).start();

                        name.animate().alpha(0f).setDuration(250).start();
                        name.setFocusable(false);
                        surname.animate().alpha(0f).setDuration(250).start();
                        surname.setFocusable(false);
                        mail.animate().alpha(0f).setDuration(250).start();
                        mail.setFocusable(false);
                        confPass.animate().alpha(0f).setDuration(250).start();
                        confPass.setFocusable(false);

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                username.animate().translationYBy((username.getHeight() + usernameMsg.getHeight()) * -1f).setDuration(250).start();
                                usernameMsg.animate().translationYBy((username.getHeight() + usernameMsg.getHeight()) * -1f).setDuration(250).start();
                                passMsg.animate().translationYBy((username.getHeight() + usernameMsg.getHeight()) * -2f).setDuration(250).start();
                                pass.animate().translationYBy((username.getHeight() + usernameMsg.getHeight()) * -2f).setDuration(250).start();
                                btn.animate().translationYBy((username.getHeight() + usernameMsg.getHeight()) * -3f).setDuration(250).start();
                                menu.animate().translationYBy((username.getHeight() + usernameMsg.getHeight()) * -3f).setDuration(250).start();
                                name.setEnabled(false);
                                surname.setEnabled(false);
                                mail.setEnabled(false);
                                confPass.setEnabled(false);
                                btn.setText("Login");
                                switchBtn.setText("Register");
                            }
                        }, 500);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                complete = true;
                            }
                        }, 1000);
                    } else {
                        register = true;
                        username.animate().translationYBy((username.getHeight() + usernameMsg.getHeight()) * 1f).setDuration(250).start();
                        usernameMsg.animate().translationYBy((username.getHeight() + usernameMsg.getHeight()) * 1f).setDuration(250).start();
                        pass.animate().translationYBy((username.getHeight() + usernameMsg.getHeight()) * 2f).setDuration(250).start();
                        passMsg.animate().translationYBy((username.getHeight() + usernameMsg.getHeight()) * 2f).setDuration(250).start();
                        btn.animate().translationYBy((username.getHeight() + usernameMsg.getHeight()) * 3f).setDuration(250).start();
                        menu.animate().translationYBy((username.getHeight() + usernameMsg.getHeight()) * 3f).setDuration(250).start();
                        btn.setText("Register");
                        switchBtn.setText("Login");
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                name.animate().alpha(1f).setDuration(250).start();
                                name.setFocusable(true);
                                name.setFocusableInTouchMode(true);
                                surname.animate().alpha(1f).setDuration(250).start();
                                surname.setFocusable(true);
                                surname.setFocusableInTouchMode(true);
                                mail.animate().alpha(1f).setDuration(250).start();
                                mail.setFocusable(true);
                                mail.setFocusableInTouchMode(true);
                                confPass.animate().alpha(1f).setDuration(250).start();
                                confPass.setFocusable(true);
                                confPass.setFocusableInTouchMode(true);

                                name.setEnabled(true);
                                surname.setEnabled(true);
                                mail.setEnabled(true);
                                confPass.setEnabled(true);
                            }
                        }, 500);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                complete = true;
                            }
                        }, 1000);
                    }
                }
            }
        });

        //Start Animations
        complete = false;
        final Button btn = (Button) findViewById(R.id.btn);
        final GridLayout menu = (GridLayout) findViewById(R.id.menu);
        name.setAlpha(0);
        surname.setAlpha(0);
        pass.setAlpha(0);
        confPass.setAlpha(0);
        username.setAlpha(0);
        mail.setAlpha(0);
        btn.setAlpha(0);
        menu.setAlpha(0);

        name.setTranslationY(25);
        surname.setTranslationY(25);
        pass.setTranslationY(25);
        confPass.setTranslationY(25);
        username.setTranslationY(25);
        mail.setTranslationY(25);
        btn.setTranslationY(25);
        menu.setTranslationY(25);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                name.animate().alpha(1f).setDuration(250).start();
                name.animate().translationYBy(-25).setDuration(250).start();
            }
        }, 1000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                surname.animate().alpha(1f).setDuration(250).start();
                surname.animate().translationYBy(-25).setDuration(250).start();
            }
        }, 1200);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                username.animate().alpha(1f).setDuration(250).start();
                username.animate().translationYBy(-25).setDuration(250).start();
            }
        }, 1400);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mail.animate().alpha(1f).setDuration(250).start();
                mail.animate().translationYBy(-25).setDuration(250).start();
            }
        }, 1600);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pass.animate().alpha(1f).setDuration(250).start();
                pass.animate().translationYBy(-25).setDuration(250).start();
            }
        }, 1800);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                confPass.animate().alpha(1f).setDuration(250).start();
                confPass.animate().translationYBy(-25).setDuration(250).start();
            }
        }, 2000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                btn.animate().alpha(1f).setDuration(250).start();
                btn.animate().translationYBy(-25).setDuration(250).start();
                menu.animate().alpha(1f).setDuration(250).start();
                menu.animate().translationYBy(-25).setDuration(250).start();
            }
        }, 2200);
        complete = true;


        mailMsg.setAlpha(0f);
        usernameMsg.setAlpha(0f);
        passMsg.setAlpha(0f);
        confPassMsg.setAlpha(0f);
        nameMsg.setAlpha(0f);
        surnameMsg.setAlpha(0f);

        pass.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (!register && i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    mainBtn(view);
                    return true;
                }
                return false;
            }
        });

        confPass.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (register && i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    mainBtn(view);
                }
                return false;

            }
        });

        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && register) {
                    if (name.getText().length() == 0) {
                        nameMsg.setText("Enter name");
                        nameMsg.animate().alpha(1f).setDuration(250).start();
                    } else {
                        nameMsg.animate().alpha(0f).setDuration(250).start();
                    }
                }
            }
        });

        surname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && register) {
                    if (surname.getText().length() == 0) {
                        surnameMsg.setText("Enter surname");
                        surnameMsg.animate().alpha(1f).setDuration(250).start();
                    } else {
                        surnameMsg.animate().alpha(0f).setDuration(250).start();
                    }
                }
            }
        });

        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b & register) {
                    if (username.getText().length() == 0) {
                        usernameMsg.setText("Enter username");
                        usernameMsg.animate().alpha(1f).setDuration(250).start();
                    } else if (username.getText().length() < 5) {
                        usernameMsg.setText("At least 5 characters");
                        usernameMsg.animate().alpha(1f).setDuration(250).start();
                    } else {
                        usernameMsg.animate().alpha(0f).setDuration(250).start();
                    }
                } else if (!b && !register) {
                    if (username.getText().length() == 0) {
                        usernameMsg.setText("Enter username");
                        usernameMsg.animate().alpha(1f).setDuration(250).start();
                    } else {
                        usernameMsg.animate().alpha(0f).setDuration(250).start();
                    }
                }
            }
        });

        mail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && register) {
                    if (!Patterns.EMAIL_ADDRESS.matcher(mail.getText().toString()).matches()) {
                        mailMsg.setText("Invalid e-mail");
                        mailMsg.animate().alpha(1f).setDuration(250).start();
                    } else {
                        mailMsg.animate().alpha(0f).setDuration(250).start();
                    }
                }
            }
        });

        pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && register) {
                    String password = pass.getText().toString();
                    if (password.length() == 0) {
                        passMsg.setTextColor(0xFFF44336);
                        passMsg.setText("Enter password");
                        passMsg.animate().alpha(1f).setDuration(250).start();
                    } else {
                        int passStrength = 0;

                        if (password.toLowerCase() != password) {
                            passStrength++;
                        }
                        if (password.length() > 8) {
                            passStrength++;
                        }
                        int numDigits = 0;
                        for (int i = 0; i < password.length(); i++) {
                            if (Character.isDigit(password.charAt(i))) {
                                numDigits++;
                                i = password.length() + 1;
                            }
                        }
                        if (numDigits == 1) {
                            passStrength++;
                        }
                        if (password.length() < 5) {
                            passMsg.setTextColor(0xFFF44336);
                            passMsg.setText("At least 5 characters");
                            passMsg.animate().alpha(1f).setDuration(250).start();
                        } else {
                            passStrength++;

                            switch (passStrength) {
                                case 1:
                                    passMsg.setText("Weak password");
                                    passMsg.setTextColor(0xFFFF9800);
                                    passMsg.animate().alpha(1f).setDuration(250).start();
                                    break;
                                case 2:
                                case 3:
                                    passMsg.setText("Medium password");
                                    passMsg.setTextColor(0xFFCDDC39);
                                    passMsg.animate().alpha(1f).setDuration(250).start();
                                    break;
                                case 4:
                                    passMsg.setText("Strong password");
                                    passMsg.setTextColor(0xFF4CAF50);
                                    passMsg.animate().alpha(1f).setDuration(250).start();
                                    break;
                            }
                        }
                    }
                } else if (!b && !register) {
                    String password = pass.getText().toString();
                    if (password.length() == 0) {
                        passMsg.setTextColor(0xFFF44336);
                        passMsg.setText("Enter password");
                        passMsg.animate().alpha(1f).setDuration(250).start();
                    } else {
                        passMsg.animate().alpha(0f).setDuration(250).start();
                    }
                }
            }
        });

        confPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && register) {
                    String password = pass.getText().toString();
                    String confPassword = confPass.getText().toString();
                    if (password.equals(confPassword) && password.length() >= 5) {
                        confPassMsg.setText("Passwords are OK");
                        confPassMsg.setTextColor(0xFF4CAF50);
                        confPassMsg.animate().alpha(1f).setDuration(250).start();
                    } else if (confPassword.equals(password) && password.length() < 5) {
                        confPassMsg.animate().alpha(0f).setDuration(250).start();
                    } else {
                        confPassMsg.setText("Passwords do not match");
                        confPassMsg.setTextColor(0xFFF44336);
                        confPassMsg.animate().alpha(1f).setDuration(250).start();
                    }
                }
                if (confPass.getText().toString().equals(pass.getText().toString()) && pass.getText().length() < 5) {
                    confPassMsg.animate().alpha(0f).setDuration(250).start();
                }
            }
        });

    }

    public void mainBtn(View view) {
        if (complete) {
            if (register) {
                validData = true;

                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                //Check Name and surname

                String nameText = name.getText().toString();
                String surnameText = surname.getText().toString();
                String usernameText = username.getText().toString();
                String emailText = mail.getText().toString().toLowerCase();
                String passwordText = pass.getText().toString();
                String confPasswordText = confPass.getText().toString();

                if (nameText.length() == 0) {
                    validData = false;
                    nameMsg.setText("Enter name");
                    nameMsg.animate().alpha(1f).setDuration(250).start();
                } else {
                    nameMsg.animate().alpha(0f).setDuration(250).start();
                }
                if (surnameText.length() == 0) {
                    validData = false;
                    surnameMsg.setText("Enter surname");
                    surnameMsg.animate().alpha(1f).setDuration(250).start();
                } else {
                    surnameMsg.animate().alpha(0f).setDuration(250).start();
                }

                if (usernameText.length() == 0) {
                    validData = false;
                    usernameMsg.setText("Enter username");
                    usernameMsg.animate().alpha(1f).setDuration(250).start();
                } else if (username.getText().length() < 5) {
                    validData = false;
                    usernameMsg.setText("At least 5 characters");
                    usernameMsg.animate().alpha(1f).setDuration(250).start();
                } else {
                    usernameMsg.animate().alpha(0f).setDuration(250).start();
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                    validData = false;
                    mailMsg.setText("Invalid e-mail");
                    mailMsg.animate().alpha(1f).setDuration(250).start();
                }

                if (passwordText.length() == 0) {
                    validData = false;
                    passMsg.setTextColor(0xFFF44336);
                    passMsg.setText("Enter password");
                    passMsg.animate().alpha(1f).setDuration(250).start();
                } else {
                    int passStrength = 0;

                    if (passwordText.toLowerCase() != passwordText) {
                        passStrength++;
                    }
                    if (passwordText.length() > 8) {
                        passStrength++;
                    }
                    int numDigits = 0;
                    for (int i = 0; i < passwordText.length(); i++) {
                        if (Character.isDigit(passwordText.charAt(i))) {
                            numDigits++;
                            i = passwordText.length() + 1;
                        }
                    }
                    if (numDigits == 1) {
                        passStrength++;
                    }
                    if (passwordText.length() < 5) {
                        validData = false;
                        passMsg.setTextColor(0xFFF44336);
                        passMsg.setText("At least 5 characters");
                        passMsg.animate().alpha(1f).setDuration(250).start();
                    } else {
                        passStrength++;

                        switch (passStrength) {
                            case 1:
                                passMsg.setText("Weak password");
                                passMsg.setTextColor(0xFFFF9800);
                                passMsg.animate().alpha(1f).setDuration(250).start();
                                break;
                            case 2:
                            case 3:
                                passMsg.setText("Medium password");
                                passMsg.setTextColor(0xFFCDDC39);
                                passMsg.animate().alpha(1f).setDuration(250).start();
                                break;
                            case 4:
                                passMsg.setText("Strong password");
                                passMsg.setTextColor(0xFF4CAF50);
                                passMsg.animate().alpha(1f).setDuration(250).start();
                                break;
                        }
                    }
                }

                if (passwordText.equals(confPasswordText)) {
                    confPassMsg.setText("Passwords are OK");
                    confPassMsg.setTextColor(0xFF4CAF50);
                    confPassMsg.animate().alpha(1f).setDuration(250).start();
                } else {
                    confPassMsg.setText("Passwords do not match");
                    confPassMsg.setTextColor(0xFFFF9800);
                    confPassMsg.animate().alpha(1f).setDuration(250).start();
                    validData = false;
                }
                if (validData) {
                    ParseUser user = new ParseUser();

                    user.setUsername(usernameText);
                    user.setEmail(emailText);
                    user.setPassword(passwordText);

                    user.put("name", nameText);
                    user.put("surname", surnameText);
                    user.put("surname", surnameText);

                    user.put("SupportPoints", 0);
                    user.put("ecoPoints", 0);
                    user.put("totalPoints", 0);
                    user.put("streak", 0);
                    user.put("maxStreak", 0);
                    user.put("location", "");
                    user.put("website", "");
                    user.put("stamps", Arrays.asList(false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false));
                    user.put("profileVisible", Arrays.asList(true, true, true, true, true));

                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Toast.makeText(getApplicationContext(), "Unknow error. Please try again later. Error code: " + e.getCode(), Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            } else {
                                findViewById(R.id.progressBar).setVisibility(View.GONE);
                                Intent i = new Intent(getApplicationContext(), EcoWall.class);
                                startActivity(i);
                            }
                        }
                    });
                }
            } else {
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                ParseUser.logInInBackground(username.getText().toString(), pass.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {

                        if (user != null) {
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            Intent i = new Intent(getApplicationContext(), EcoWall.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(getApplicationContext(), "Bad login", Toast.LENGTH_LONG).show();
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            e.printStackTrace();
                        }

                    }
                });
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
