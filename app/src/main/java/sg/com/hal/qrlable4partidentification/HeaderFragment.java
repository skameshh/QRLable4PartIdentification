package sg.com.hal.qrlable4partidentification;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HeaderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HeaderFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM_TITLE = "title__";
    private static String TAG = "HALPhotoPrint";
    View rootView = null;
    private ImageView btnMenu;
    private ImageView imgBack;
    private TextView lbl_title;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String str_title;

    public HeaderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HeaderFragment.
     */
    // TODO: Rename and change types and number of parameters
   /* public static HeaderFragment newInstance(String param1, String param2) {
        HeaderFragment fragment = new HeaderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    public static HeaderFragment newInstance(String title) {
        HeaderFragment fragment = new HeaderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            str_title = getArguments().getString(ARG_PARAM_TITLE);
           // mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_header, container, false);
        imgBack = (ImageView)rootView.findViewById(R.id.imgBack);
        btnMenu = (ImageView)rootView.findViewById(R.id.imgMenu);
        lbl_title = (TextView)rootView.findViewById(R.id.lbl_title);
        lbl_title.setText(str_title);

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                doClickMnu();
            }
        });
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_header, container, false);
        return rootView;
    }

    public void doClickMnu() {
        final Context ctx = this.getContext();
        //Creating the instance of PopupMenu
        PopupMenu popup= new PopupMenu(ctx, btnMenu);
        popup.getMenuInflater().inflate(R.menu.menu_hal,popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item){
                Toast.makeText(ctx,"You Clicked:"+item.getTitle(),Toast.LENGTH_SHORT).show();
                if(item.getTitle().equals("Settings")){
                    gotoSettings();
                }
                return true;
            }
        });
        popup.show();//showing popup menu
    }

    public void gotoSettings(){
        try {
            // Intent intent = new Intent(rootView.getContext(), SettingsActivity.class);
            //startActivity(intent);
            Log.v(TAG,"Settings menu called ");
        }catch(Exception e){
            Log.v(TAG,"Error in "+e.getLocalizedMessage());
        }
    }
}