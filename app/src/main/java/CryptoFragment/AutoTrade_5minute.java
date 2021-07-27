package CryptoFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.autotrade.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AutoTrade_5minute#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AutoTrade_5minute extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AutoTrade_5minute() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AutoTrade_5minute.
     */
    // TODO: Rename and change types and number of parameters
    public static AutoTrade_5minute newInstance(String param1, String param2) {
        AutoTrade_5minute fragment = new AutoTrade_5minute();
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
        //Initialize view
        View view = inflater.inflate(R.layout.fragment_auto_trade_5minute, container, false);

//        //Initialize and assign variable
//        TextView textView = view.findViewById(R.id.autotrade_5min_text);
//
//        //Get title
//        String sTitle = getArguments().getString("title");
//
//        //Set title on text view
//        textView.setText(sTitle);

        //Return view
        return view;
    }
}