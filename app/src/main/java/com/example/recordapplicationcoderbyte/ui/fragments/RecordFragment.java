package com.example.recordapplicationcoderbyte.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recordapplicationcoderbyte.R;
import com.example.recordapplicationcoderbyte.ui.activities.CameraActivity;

import androidx.fragment.app.Fragment;


public class RecordFragment extends Fragment {
    EditText txtTitle;
    SeekBar seekBar;
    long getSeconds;
    long perValue;
    TextView durTxt;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.fragment_record, container, false);
        Button recordBtn = RootView.findViewById(R.id.button);
        recordBtn.setOnClickListener(btnClick);
        txtTitle = RootView.findViewById(R.id.editText);
        durTxt = RootView.findViewById(R.id.textView5);
        seekBar = RootView.findViewById(R.id.seekBar2);
        seekBar.setMax(165);
        getSeconds = 16;
        durTxt.setText(""+(getSeconds-1)+" secs");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                getSeconds = progress + 16 ;
                perValue = getSeconds-1;
                if(perValue>60){
                    if (perValue%60 == 0 ) {
                        durTxt.setText(perValue / 60 +" min ");
                    }
                    else durTxt.setText(perValue / 60 +" min " + perValue%60+ " sec");
                }
                else durTxt.setText(perValue + " sec");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Inflate the layout for this fragment
        return RootView;
    }
    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            long seconds = getSeconds;
            String title = txtTitle.getText().toString();
            if (!title.matches("")){
                Intent intent = new Intent(getContext(), CameraActivity.class);
                intent.putExtra("title",title);
                intent.putExtra("seconds",seconds);
                startActivity(intent);
            }
            else {
                txtTitle.setFocusableInTouchMode(true);
                txtTitle.requestFocus();
                Toast.makeText(getContext(),"You must enter a file name",Toast.LENGTH_SHORT).show();
            }
        }
    };
}