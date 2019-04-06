package com.example.unpasuserdemo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.unpasuserdemo.R;
import com.example.unpasuserdemo.models.ModelJadwal;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterJadwal extends RecyclerView.Adapter<AdapterJadwal.HolderData> {

    private List<ModelJadwal> listJadwal;

    public AdapterJadwal(List<ModelJadwal> listJadwal){
        this.listJadwal = listJadwal;
    }

    @NonNull
    @Override
    public AdapterJadwal.HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_jadwal_mahasiswa,parent,false);
        return new HolderData(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterJadwal.HolderData holder, int position) {
        ModelJadwal modelJadwal = listJadwal.get(position);
        holder.rjm_matakuliah.setText(modelJadwal.getNama_matakuliah());
        holder.rjm_dosen.setText(modelJadwal.getNama_dosen());
        holder.rjm_mulai.setText(modelJadwal.getJam_mulai());
        holder.rjm_ruangan.setText(modelJadwal.getNama_ruangan());
    }

    @Override
    public int getItemCount() {
        return listJadwal.size();
    }

    class HolderData extends RecyclerView.ViewHolder{

        TextView rjm_matakuliah, rjm_dosen, rjm_mulai, rjm_ruangan;
        HolderData(@NonNull View v) {
            super(v);
            rjm_matakuliah = v.findViewById(R.id.rpm_matakuliah);
            rjm_dosen = v.findViewById(R.id.rpm_dosen);
            rjm_mulai = v.findViewById(R.id.rpm_jam);
            rjm_ruangan = v.findViewById(R.id.rpm_ruangan);
        }
    }
}
