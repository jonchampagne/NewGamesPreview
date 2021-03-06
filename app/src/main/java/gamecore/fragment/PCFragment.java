package gamecore.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;

import gamecore.R;
import gamecore.activities.SubActivity;
import gamecore.adapters.AdapterPCgames;
import gamecore.callbacks.PCgamesLoadedListener;
import gamecore.materialtest.DividerItemDecoration;
import gamecore.materialtest.MyApp;
import gamecore.network.VolleySingleton;
import gamecore.pojo.Game;
import gamecore.task.TaskLoadMain;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PCFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PCFragment extends Fragment implements AdapterPCgames.ClickListener, PCgamesLoadedListener, SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String STATE_GAMES = "state_games";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private RequestQueue requestQueue;
    private RecyclerView listPCnewgames;
    private AdapterPCgames adapterPCgames;
    ArrayList<Game> listPCGames = new ArrayList<>();
    private TextView textVolleyError;
    private SwipeRefreshLayout swipeRefreshLayout;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MmoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PCFragment newInstance(String param1, String param2) {
        PCFragment fragment = new PCFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_GAMES, listPCGames);
    }*/

    public PCFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void hadleVolleyError(VolleyError error) {

        textVolleyError.setVisibility(View.VISIBLE);
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            textVolleyError.setText(R.string.error_timeout);

        } else if (error instanceof NetworkError) {
            textVolleyError.setText(R.string.error_network);

        } else if (error instanceof ParseError) {
            textVolleyError.setText(R.string.error_parser);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pc, container, false);
        textVolleyError = (TextView) view.findViewById(R.id.textVolleyError);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeGames);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(Color.GRAY);
        listPCnewgames = (RecyclerView) view.findViewById(R.id.listPCgames);
        listPCnewgames.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        adapterPCgames = new AdapterPCgames(getActivity());
        adapterPCgames.setClickListener(this);
        listPCnewgames.setAdapter(adapterPCgames);
        listPCnewgames.setLayoutManager(new LinearLayoutManager(getActivity()));
        //if (savedInstanceState != null) {
        //    listPCGames = savedInstanceState.getParcelableArrayList(STATE_GAMES);
        //} else {
            listPCGames = MyApp.getWritableDatabase().getAllgames();
            if (listPCGames.isEmpty()) {
                new TaskLoadMain(this).execute();
            }

        adapterPCgames.setGamelist(listPCGames);
        return view;
    }


    @Override
    public void itemClicked(View view, int position) {
        int itemPosition = listPCnewgames.getChildPosition(view);
        Log.e("detailUrl",listPCGames.get(itemPosition).getDetailUrl());
        startActivity(new Intent(getActivity(), SubActivity.class)
                .putExtra("gameDetailUrl", listPCGames.get(position).getDetailUrl()));
    }

    @Override
    public void onPCgamesLoaded(ArrayList<Game> listGames) {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        listPCGames = listGames;
        adapterPCgames.setGamelist(listGames);
    }

    @Override
    public void onRefresh() {
        new TaskLoadMain(this).execute();
    }
}
