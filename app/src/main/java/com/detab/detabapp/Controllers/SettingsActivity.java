package com.detab.detabapp.Controllers;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.detab.detabapp.Models.PermissionHelper;
import com.detab.detabapp.Providers.GPSTracker;
import com.detab.detabapp.R;

public class SettingsActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        PermissionHelper.AskPermissions(this);
        GPSTracker.CheckIsGpsProviderEnabled(getApplicationContext());
    }

    public void btnSaveSettings_Click(View view)
    {
        Intent intent = new Intent(this, NewMap.class);
        EditText txtTolerance = (EditText) findViewById(R.id.txtTolerance);
        EditText txtChecks = (EditText) findViewById(R.id.txtChecks);
        intent.putExtra("TOLERANCE", txtTolerance.getText().toString());
        intent.putExtra("CHECKS", txtChecks.getText().toString());
        startActivity(intent);
    }
}
