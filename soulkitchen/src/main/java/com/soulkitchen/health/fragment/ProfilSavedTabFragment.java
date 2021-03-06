package com.soulkitchen.health.fragment;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.soulkitchen.health.adapters.CardViewAdapter;
import com.soulkitchen.health.R;
import com.soulkitchen.health.pojo.Recipies;
import com.soulkitchen.health.pojo.SavedRecipies;
import com.soulkitchen.health.utils.Session;

import java.util.List;

/**
 * Created by serifenuruysal on 07/03/17.
 */

public class ProfilSavedTabFragment extends BaseFragment {

//    private Toolbar mToolbar;
    private List<Recipies> recipieList;
    private CardViewAdapter adapter;
    RecyclerView mRecyclerView;

    public static ProfilSavedTabFragment newInstance() {
        ProfilSavedTabFragment f = new ProfilSavedTabFragment();
        Bundle localBundle = new Bundle(1);
        f.setArguments(localBundle);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profil_saved_tab, container, false);
//        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
//        mToolbar.inflateMenu(R.menu.menu_recipie);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getSavedRecipies();

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        adapter = new CardViewAdapter(getContext(), recipieList, new CardViewAdapter.CardViewAdapterListener() {
            @Override
            public void onClickCard(Recipies recipie) {
                ProfilSavedTabFragment.this.setFragment(ProfilSavedTabFragment.this, RecipieDetailFragment.newInstance(recipie));
            }

            @Override
            public void onClickActionFinish() {
                getSavedRecipies();
            }
        }, true);



        mRecyclerView.setAdapter(adapter);
    }

    private void getSavedRecipies() {

        String whereClause = "ownerId ='"+(String) Session.getSession().getUser().getUserId()+"'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause( whereClause );

        SavedRecipies.findAsync(dataQuery, new AsyncCallback<BackendlessCollection<SavedRecipies>>() {
            @Override
            public void handleResponse(BackendlessCollection<SavedRecipies> recipies) {
                if (recipies!=null&&recipies.getData()!=null&&recipies.getData().size()>0){
                    String userSavedRecipies="";
                    List<SavedRecipies> sr=recipies.getData();
                    for (int i=0;i<sr.size();i++){
                        userSavedRecipies=userSavedRecipies+"'"+sr.get(i).getRecipieId().trim()+"'";
                        if (i<sr.size()-1){
                            userSavedRecipies=userSavedRecipies+",";
                        }
                    }

                    String whereClause = "objectId IN (" + userSavedRecipies+")";
                    BackendlessDataQuery dataQuery = new BackendlessDataQuery();
                    dataQuery.setWhereClause(whereClause);
                    Recipies.findAsync(dataQuery, new AsyncCallback<BackendlessCollection<Recipies>>() {
                        @Override
                        public void handleResponse(BackendlessCollection<Recipies> recipies) {
                            if (recipies != null && recipies.getData() != null && recipies.getData().size() > 0) {
                                recipieList = recipies.getData();
                                adapter.setList(recipieList);
                                adapter.notifyDataSetChanged();
                            }
                            recipies.getTableName();

                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                        }
                    });
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {

            }
        });


    }

}