package com.github.android.lvrn.lvrnproject.view.fragment.trash.impl;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;

import com.github.android.lvrn.lvrnproject.persistent.entity.Note;
import com.github.android.lvrn.lvrnproject.service.core.NoteService;
import com.github.android.lvrn.lvrnproject.util.CurrentState;
import com.github.android.lvrn.lvrnproject.util.PaginationArgs;
import com.github.android.lvrn.lvrnproject.view.fragment.trash.TrashFragment;
import com.github.android.lvrn.lvrnproject.view.fragment.trash.TrashPresenter;
import com.github.android.lvrn.lvrnproject.view.listener.RecyclerViewOnScrollListener;
import com.github.android.lvrn.lvrnproject.view.listener.SearchViewOnQueryTextListener;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.ReplaySubject;

/**
 * @author Andrii Bei <psihey1@gmail.com>
 */

public class TrashPresenterImpl implements TrashPresenter, MenuItemCompat.OnActionExpandListener {
    private NoteService mNoteService;

    private TrashFragment mTrashFragment;

    private RecyclerViewOnScrollListener mRecyclerViewOnScrollLister;

    private Disposable mSearchDisposable;

    private Disposable mPaginationDisposable;

    private Disposable mFoundedPaginationDisposable;

    private ReplaySubject<PaginationArgs> mPaginationSubject;

    private ReplaySubject<PaginationArgs> mFoundedPaginationSubject;

    private List<Note> mNotes;

    public TrashPresenterImpl(NoteService mNoteService) {
        (this.mNoteService = mNoteService).openConnection();
    }

    @Override
    public void bindView(TrashFragment trashFragment) {
        mTrashFragment = trashFragment;
        if (!mNoteService.isConnectionOpened()) {
            mNoteService.openConnection();
        }
    }

    @Override
    public void unbindView() {
        mTrashFragment = null;
        mNoteService.closeConnection();
    }

    @Override
    public void subscribeRecyclerViewForPagination(RecyclerView recyclerView) {
        initPaginationSubject();
        initFoundedPaginationSubject();
        mRecyclerViewOnScrollLister = new RecyclerViewOnScrollListener(mPaginationSubject);
        recyclerView.addOnScrollListener(mRecyclerViewOnScrollLister);
    }

    @Override
    public void subscribeSearchView(MenuItem searchItem) {
        MenuItemCompat.setOnActionExpandListener(searchItem, this);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        initSearchSubject(searchView);
    }

    /**
     * A method which expands search view and changes a subject of recycler view's onScrollListener.
     * @param item
     * @return
     */
    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        mTrashFragment.startSearchMode();
        mRecyclerViewOnScrollLister.changeSubject(mFoundedPaginationSubject);
        return true;
    }

    /**
     * A method which collapses search view and changes a subject of recycler view's onScrollListener.
     * @param item
     * @return
     */
    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        mTrashFragment.startNormalMode();
        mRecyclerViewOnScrollLister.changeSubject(mPaginationSubject);
        addFirstFoundedItemsToList(mNoteService.getByProfile(CurrentState.profileId, true, new PaginationArgs()));
        mTrashFragment.updateRecyclerView();
        return true;
    }

    @Override
    public void disposePaginationAndSearch() {
        if (mSearchDisposable != null && !mSearchDisposable.isDisposed()) {
            mSearchDisposable.dispose();
        }
        if (mPaginationDisposable != null && !mPaginationDisposable.isDisposed()) {
            mPaginationDisposable.dispose();
        }
        if (mFoundedPaginationDisposable != null && !mFoundedPaginationDisposable.isDisposed()) {
            mFoundedPaginationDisposable.dispose();
        }
    }

    @Override
    public List<Note> getNotesForAdapter() {
        mNotes = mNoteService.getByProfile(CurrentState.profileId, false, new PaginationArgs());
        return mNotes;
    }

    @Override
    public void removeNoteForever() {
        //TODO: implement
    }

    @Override
    public void restoreNote() {
        //TODO: Implement
    }

    private void initPaginationSubject() {
        mPaginationDisposable = (mPaginationSubject = ReplaySubject.create())
                .observeOn(Schedulers.io())
                .map(paginationArgs -> mNoteService.getByProfile(CurrentState.profileId, false, paginationArgs))
                .filter(notes -> !notes.isEmpty())
                .map(newNotes -> mNotes.addAll(newNotes))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> mTrashFragment.updateRecyclerView(),
                        throwable -> {/*TODO: find out what can happen here*/});
    }

    private void initSearchSubject(SearchView searchView) {
        ReplaySubject<String> searchQuerySubject;

        mSearchDisposable = (searchQuerySubject = ReplaySubject.create())
                .debounce(400, TimeUnit.MILLISECONDS)
                .filter(title -> title.length() > 0)
                .map(title -> mNoteService.getByTitle(CurrentState.profileId, title, false, new PaginationArgs()))
                .map(this::addFirstFoundedItemsToList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> mTrashFragment.updateRecyclerView(),
                        throwable -> {/*TODO: find out what can happen here*/});

        searchView.setOnQueryTextListener(new SearchViewOnQueryTextListener(searchQuerySubject));
    }

    private boolean addFirstFoundedItemsToList(List<Note> firstFoundedNotes) {
        mRecyclerViewOnScrollLister.resetListener();
        mNotes.clear();
        mNotes.addAll(firstFoundedNotes);
        return true;
    }

    private void initFoundedPaginationSubject() {
        mFoundedPaginationDisposable = (mFoundedPaginationSubject = ReplaySubject.create())
                .observeOn(Schedulers.io())
                .map(paginationArgs -> mNoteService.getByTitle(CurrentState.profileId, mTrashFragment.getSearchQuery(), false, paginationArgs))
                .filter(notes -> !notes.isEmpty())
                .map(newNotes -> mNotes.addAll(newNotes))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> mTrashFragment.updateRecyclerView(),
                        throwable -> {/*TODO: find out what can happen here*/});
    }
}