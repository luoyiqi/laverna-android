package com.github.android.lvrn.lvrnproject.view.adapter.datapostset.impl;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.android.lvrn.lvrnproject.R;
import com.github.android.lvrn.lvrnproject.persistent.entity.Note;
import com.github.android.lvrn.lvrnproject.view.adapter.datapostset.DataPostSetAdapter;
import com.github.android.lvrn.lvrnproject.view.fragment.entitieslist.core.favouriteslist.FavouritesListFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * @author Andrii Bei <psihey1@gmail.com>
 */

public class FavouritesListAdapter extends RecyclerView.Adapter<FavouritesListAdapter.FavouriteViewHolder> implements DataPostSetAdapter<Note> {

    private FavouritesListFragment mAllNotesFragment;

    private List<Note> mNotes;

    public FavouritesListAdapter(FavouritesListFragment allNotesFragment) {
        mAllNotesFragment = allNotesFragment;
    }

    @Override
    public FavouriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new FavouriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavouriteViewHolder holder, int position) {
        holder.tvTitle.setText(mNotes.get(position).getTitle());
        holder.tvPromptText.setText(mNotes.get(position).getContent());

        holder.itemView.setOnClickListener(v -> mAllNotesFragment.showSelectedNote(mNotes.get(position)));
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    @Override
    public void setData(List<Note> data) {
        mNotes = data;
    }

    //TODO: maybe create one class for it.
    class FavouriteViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title_note)
        TextView tvTitle;

        @BindView(R.id.tv_date_created_note) TextView tvDate;

        @BindView(R.id.tv_prompt_text_note) TextView tvPromptText;

        @BindView(R.id.im_btn_favorite) ImageButton imBtnFavorite;

        FavouriteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}


