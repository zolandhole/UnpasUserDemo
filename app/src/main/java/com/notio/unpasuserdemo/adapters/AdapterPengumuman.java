package com.notio.unpasuserdemo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.notio.unpasuserdemo.R;
import com.notio.unpasuserdemo.models.ModelPengumuman;

import java.util.List;

public class AdapterPengumuman extends RecyclerView.Adapter<AdapterPengumuman.HolderData> {

    private List<ModelPengumuman> listPengumuman;
    private String nama;

    public AdapterPengumuman(List<ModelPengumuman> listPengumuman, String nama){
        this.listPengumuman = listPengumuman;
        this.nama = nama;
    }

    @NonNull
    @Override
    public AdapterPengumuman.HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_pengumuman, parent, false);
        return  new HolderData(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPengumuman.HolderData holder, int position) {
        ModelPengumuman modelPengumuman = listPengumuman.get(position);
        if (!nama.equals(modelPengumuman.getTITLE())){
            holder.main_title.setText(modelPengumuman.getTITLE());
        } else {
            holder.main_title.setText(R.string.anda);
        }
        holder.main_pesan.setText(modelPengumuman.getPESAN());
        holder.main_jam.setText(modelPengumuman.getUPLOAD_DATE());
    }

    @Override
    public int getItemCount() {
        return listPengumuman.size();
    }

    class HolderData extends RecyclerView.ViewHolder{
        TextView main_title, main_pesan, main_jam;
        HolderData(@NonNull View itemView) {
            super(itemView);
            main_title = itemView.findViewById(R.id.main_pengirim);
            main_pesan = itemView.findViewById(R.id.main_pesan);
            main_jam = itemView.findViewById(R.id.main_jam);
        }
    }
}
