package com.github.android.lvrn.lvrnproject.view.fragment.entitieslist.core.favouriteslist.impl;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.android.lvrn.lvrnproject.LavernaApplication;
import com.github.android.lvrn.lvrnproject.R;
import com.github.android.lvrn.lvrnproject.persistent.entity.Note;
import com.github.android.lvrn.lvrnproject.service.core.NoteService;
import com.github.android.lvrn.lvrnproject.view.activity.noteeditor.impl.NoteEditorActivityImpl;
import com.github.android.lvrn.lvrnproject.view.adapter.impl.FavouritesAdapter;
import com.github.android.lvrn.lvrnproject.view.fragment.entitieslist.core.favouriteslist.FavouritesListFragment;
import com.github.android.lvrn.lvrnproject.view.fragment.entitieslist.core.favouriteslist.FavouritesListPresenter;
import com.github.android.lvrn.lvrnproject.view.fragment.singlenote.SingleNoteFragmentImpl;
import com.github.android.lvrn.lvrnproject.view.util.consts.BundleKeysConst;
import com.github.android.lvrn.lvrnproject.view.util.consts.TagFragmentConst;
import com.orhanobut.logger.Logger;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author Andrii Bei <psihey1@gmail.com>
 */

public class FavouritesListFragmentImpl extends Fragment implements FavouritesListFragment {

    public static final String TOOLBAR_TITLE = "Favourites";

    @Inject NoteService mNoteService;

    @BindView(R.id.recycler_view_all_notes) RecyclerView mNotesRecyclerView;

    @BindView(R.id.floating_action_menu_all_notes) FloatingActionsMenu floatingActionsMenu;

    private Unbinder mUnbinder;

    private FavouritesAdapter mFavouritesRecyclerViewAdapter;

    private SearchView mSearchView;

    private MenuItem mMenuSearch;

//    TODO: introduce in future milestones
//    private MenuItem menuSync, menuSortBy, menuSettings, menuAbout;

    private FavouritesListPresenter mFavouritesListPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_notes, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        LavernaApplication.getsAppComponent().inject(this);


        mFavouritesListPresenter = new FavouritesListPresenterImpl(mNoteService);

        setUpToolbar();
        initRecyclerView();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mFavouritesListPresenter == null) {
            mFavouritesListPresenter = new FavouritesListPresenterImpl(mNoteService);
        }
        mFavouritesListPresenter.bindView(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        floatingActionsMenu.collapse();
        mFavouritesListPresenter.unbindView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_all_notes, menu);

        mMenuSearch = menu.findItem(R.id.item_action_search);
//        TODO: introduce in future milestones
//        menuSync = menu.findItem(R.id.item_action_sync);
//        menuAbout = menu.findItem(R.id.item_about);
//        menuSortBy = menu.findItem(R.id.item_sort_by);
//        menuSettings = menu.findItem(R.id.item_settings);

        mSearchView = (SearchView) MenuItemCompat.getActionView(mMenuSearch);

        mFavouritesListPresenter.subscribeSearchView(mMenuSearch);

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        mFavouritesListPresenter.disposePaginationAndSearch();
    }

    @Override
    public void updateRecyclerView() {
        mFavouritesRecyclerViewAdapter.notifyDataSetChanged();
        Logger.d("Recycler view is updated");
    }

    @Override
    public void showSelectedNote(Note note) {
        SingleNoteFragmentImpl singleNoteFragmentImpl = new SingleNoteFragmentImpl();

        Bundle bundle = new Bundle();
        bundle.putParcelable(BundleKeysConst.BUNDLE_NOTE_OBJECT_KEY, note);
        singleNoteFragmentImpl.setArguments(bundle);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.constraint_container, singleNoteFragmentImpl, TagFragmentConst.TAG_SINGLE_NOTE_FRAGMENT)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public String getSearchQuery() {
        return mSearchView.getQuery().toString();
    }

    @Override
    public void switchToSearchMode() {
        floatingActionsMenu.collapse();
        floatingActionsMenu.setVisibility(View.GONE);
//        TODO: introduce in future milestones
//        menuSync.setVisible(false);
//        menuAbout.setVisible(false);
//        menuSortBy.setVisible(false);
//        menuSettings.setVisible(false);
        mSearchView.setQueryHint(getString(R.string.fragment_all_notes_menu_search_query_hint));
        mSearchView.requestFocus();
        Drawable bottomUnderline = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            bottomUnderline = getResources().getDrawable(R.drawable.search_view_bottom_underline, null);
        }
        mSearchView.setBackground(bottomUnderline);
    }

    @Override
    public void switchToNormalMode() {
        floatingActionsMenu.setVisibility(View.VISIBLE);
//        TODO: introduce in future milestones
//        menuSync.setVisible(true);
//        menuAbout.setVisible(true);
//        menuSortBy.setVisible(true);
//        menuSettings.setVisible(true);
    }


    /**
     * A method which hears when user click on button and opens new activity
     */
    @OnClick(R.id.floating_btn_start_note)
    public void startNoteEditorActivity() {
        getActivity().startActivity(new Intent(getActivity(), NoteEditorActivityImpl.class));
        getActivity().finish();
    }

    @OnClick(R.id.floating_btn_start_notebook)
    public void openNotebooksCreationDialog() {
        //TODO: Change method with implement need functionality
        floatingActionsMenu.collapse();
    }

    /**
     * A method which initializes recycler view with data
     */
    private void initRecyclerView() {
        mNotesRecyclerView.setHasFixedSize(true);

        mNotesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mFavouritesRecyclerViewAdapter = new FavouritesAdapter(this);
        mFavouritesListPresenter.setDataToAdapter(mFavouritesRecyclerViewAdapter);
        mNotesRecyclerView.setAdapter(mFavouritesRecyclerViewAdapter);

        mFavouritesListPresenter.subscribeRecyclerViewForPagination(mNotesRecyclerView);
    }

    /**
     * A method which sets defined view of toolbar
     */
    private void setUpToolbar() {
        setHasOptionsMenu(true);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(TOOLBAR_TITLE);
        }
    }
}