package com.soulkitchen.health.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.backendless.BackendlessCollection;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.soulkitchen.health.server.DefaultCallback;
import com.soulkitchen.health.adapters.MyCategoryAdapter;
import com.soulkitchen.health.R;
import com.soulkitchen.health.pojo.Categories;
import com.soulkitchen.health.view.PagerSlidingTabStrip;

import java.util.List;

import static com.backendless.media.SessionBuilder.TAG;

/**
 * Created by serifenuruysal on 07/03/17.
 */

public class SearchFragment extends BaseFragment  implements AppBarLayout.OnOffsetChangedListener {
    ViewPager mViewPager;
    PagerSlidingTabStrip tabs;
    private List<Categories> categoryList;
    ImageView appBarImageView;
    private AppBarLayout mAppBarLayout;
    FrameLayout fr;
    @Override
    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        View view = paramLayoutInflater.inflate(R.layout.fragment_search, paramViewGroup, false);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        tabs=(PagerSlidingTabStrip)view.findViewById(R.id.tabs);
        appBarImageView=(ImageView)view.findViewById(R.id.app_bar_image);
        mAppBarLayout   = (AppBarLayout) view.findViewById(R.id.appbar);
        mAppBarLayout.addOnOffsetChangedListener(this);
        fr= (FrameLayout) view.findViewById(R.id.main_framelayout_title);

        FloatingSearchView mSearchView;
        mSearchView = (FloatingSearchView)view.findViewById(R.id.floating_search_view);
        mSearchView.setOnLeftMenuClickListener(
                new FloatingSearchView.OnLeftMenuClickListener() {
                    @Override
                    public void onMenuOpened() {

                    }

                    @Override
                    public void onMenuClosed() {

                    }
                });

        mSearchView.setOnHomeActionClickListener(
                new FloatingSearchView.OnHomeActionClickListener() {
                    @Override
                    public void onHomeClicked() {


                    }
                });

        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {

            }

        });
        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView, SearchSuggestion item, int itemPosition) {

                //here you can set some attributes for the suggestion's left icon and text. For example,
                //you can choose your favorite image-loading library for setting the left icon's image.
            }

        });
        retrieveBasicCategoriesRecord();

        return view;
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

//        handleAlphaOnTitle(percentage,fr);
//        handleToolbarTitleVisibility(percentage);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static final SearchFragment newInstance(String paramString) {
        SearchFragment f = new SearchFragment();
        Bundle localBundle = new Bundle(1);
        localBundle.putString("EXTRA_MESSAGE", paramString);
        f.setArguments(localBundle);
        return f;
    }

    private void retrieveBasicCategoriesRecord()
    {
        if (categoryList!=null){
            setViewPager();
            return;

        }

        BackendlessDataQuery query = new BackendlessDataQuery();
        Categories.findAsync( query, new DefaultCallback<BackendlessCollection<Categories>>( getActivity())
        {
            @Override
            public void handleResponse( BackendlessCollection<Categories> recipies )
            {
                super.handleResponse( recipies );
                if (recipies!=null&&recipies.getData()!=null&&recipies.getData().size()>0){
                    categoryList=recipies.getData();
                    setViewPager();
                }
            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                Log.e(TAG, "handleFault: "+fault.getMessage(),null );
            }
        } );
    }

    private void setViewPager() {
        mViewPager.setAdapter(new MyCategoryAdapter(getChildFragmentManager(),categoryList )) ;


        mViewPager.setOffscreenPageLimit(mViewPager.getAdapter().getCount());
        mViewPager.setCurrentItem(0);
        Glide.with(getContext()).load(categoryList.get(0).getCategoryImage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).centerCrop().into(appBarImageView);

        tabs.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Glide.with(getContext()).load(categoryList.get(position).getCategoryImage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).centerCrop().into(appBarImageView);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        List<Categories> recipieList;
        public MyPagerAdapter(FragmentManager fm, List<Categories> recipieList) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return recipieList.get(position).getCategoryNameTr();
        }

        @Override
        public int getCount() {
            return recipieList!=null?recipieList.size():0;
        }

        @Override
        public Fragment getItem(int position) {
            return CategoryFragment.newInstance(position);
        }

    }


}