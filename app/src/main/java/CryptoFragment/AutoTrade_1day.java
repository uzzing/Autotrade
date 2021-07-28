package CryptoFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.project.autotrade.R;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AutoTrade_1day#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AutoTrade_1day extends Fragment {

    int myProgress = 0;
    ProgressBar progressBarView;
    Button btn_start;
    Button btn_stop;
    TextView tv_time;
    EditText et_timer;
    int progress;
    CountDownTimer countDownTimer;
    int endTime = 86390;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AutoTrade_1day() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AutoTrade_1day.
     */
    // TODO: Rename and change types and number of parameters
    public static AutoTrade_1day newInstance(String param1, String param2) {
        AutoTrade_1day fragment = new AutoTrade_1day();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_auto_trade_1day, container, false);

        progressBarView = (ProgressBar) view.findViewById(R.id.view_progress_bar_1day);
        btn_start = (Button) view.findViewById(R.id.btn_start_1day);
        tv_time= (TextView) view.findViewById(R.id.tv_timer_1day);
        btn_stop = (Button) view.findViewById(R.id.btn_stop_1day);
//        et_timer = (EditText) view.findViewById(R.id.et_timer_1day);

        /*Animation*/
        RotateAnimation makeVertical = new RotateAnimation(0, -90, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        makeVertical.setFillAfter(true);
        progressBarView.startAnimation(makeVertical);
        progressBarView.setSecondaryProgress(endTime);
        progressBarView.setProgress(0);


        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fn_countdown();
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                tv_time.setText("00:00:00");
            }
        });

        return view;
    }

    private void fn_countdown() {

            myProgress = 0;

            try {
                countDownTimer.cancel();

            } catch (Exception e) {

            }

//            String timeInterval = et_timer.getText().toString();
            progress = 1;
//            endTime = Integer.parseInt(timeInterval); // up to finish time

            countDownTimer = new CountDownTimer(endTime * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    setProgress(progress, endTime);
                    progress = progress + 1;
                    int seconds = (int) (millisUntilFinished / 1000) % 60;
                    int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                    int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                    String newtime = hours + ":" + minutes + ":" + seconds;

                    if (newtime.equals("0:0:0")) {
                        tv_time.setText("00:00:00");
                    } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(minutes).length() == 1) && (String.valueOf(seconds).length() == 1)) {
                        tv_time.setText("0" + hours + ":0" + minutes + ":0" + seconds);
                    } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(minutes).length() == 1)) {
                        tv_time.setText("0" + hours + ":0" + minutes + ":" + seconds);
                    } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(seconds).length() == 1)) {
                        tv_time.setText("0" + hours + ":" + minutes + ":0" + seconds);
                    } else if ((String.valueOf(minutes).length() == 1) && (String.valueOf(seconds).length() == 1)) {
                        tv_time.setText(hours + ":0" + minutes + ":0" + seconds);
                    } else if (String.valueOf(hours).length() == 1) {
                        tv_time.setText("0" + hours + ":" + minutes + ":" + seconds);
                    } else if (String.valueOf(minutes).length() == 1) {
                        tv_time.setText(hours + ":0" + minutes + ":" + seconds);
                    } else if (String.valueOf(seconds).length() == 1) {
                        tv_time.setText(hours + ":" + minutes + ":0" + seconds);
                    } else {
                        tv_time.setText(hours + ":" + minutes + ":" + seconds);
                    }

                }

                @Override
                public void onFinish() {
                    setProgress(progress, endTime);


                }
            };
            countDownTimer.start();

    }

    public void setProgress(int startTime, int endTime) {
        progressBarView.setMax(endTime);
        progressBarView.setSecondaryProgress(endTime);
        progressBarView.setProgress(startTime);

    }
}