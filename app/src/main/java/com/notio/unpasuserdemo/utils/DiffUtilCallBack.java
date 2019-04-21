package com.notio.unpasuserdemo.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.notio.unpasuserdemo.models.ModelPengumuman;

import java.util.List;

public class DiffUtilCallBack extends DiffUtil.Callback {

    private List<ModelPengumuman> oldList;
    private List<ModelPengumuman> newList;

    public DiffUtilCallBack(List<ModelPengumuman> oldList, List<ModelPengumuman> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItemPosition == newItemPosition;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
