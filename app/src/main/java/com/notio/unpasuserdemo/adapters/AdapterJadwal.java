package com.notio.unpasuserdemo.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.notio.unpasuserdemo.R;
import com.notio.unpasuserdemo.models.ModelJadwal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterJadwal extends RecyclerView.Adapter<AdapterJadwal.HolderData> {

    private List<ModelJadwal> listJadwal;
    private static final String TAG = "AdapterJadwal";
    private long perbedaanJam, perbedaanMenit;

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

        String jamMulai = modelJadwal.getJam_mulai();
        String jamSelesai = modelJadwal.getJam_selesai();

        Date dateMulai, dateSelesai;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

        try {
            dateMulai = sdf.parse(jamMulai);
            dateSelesai = sdf.parse(jamSelesai);

            long diff = dateSelesai.getTime() - dateMulai.getTime();
            perbedaanJam = diff / (60*60*1000) % 24;
            perbedaanMenit = diff / (60*1000) % 60;

        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "onBindViewHolder: " + e);
        }

        String durasi = "durasi ";
        Log.e(TAG, "onBindViewHolder: perbedaan waktu "+ perbedaanJam + " jam" + perbedaanMenit + " menit");

        if (perbedaanJam != 0 && perbedaanMenit != 0){
            durasi += perbedaanJam + " jam " + perbedaanMenit + " menit";
        } else if (perbedaanJam != 0){
            durasi += perbedaanJam + " jam";
        } else if (perbedaanMenit != 0){
            durasi += perbedaanMenit + " menit";
        } else {
            durasi = "";
        }

        holder.rjm_matakuliah.setText(modelJadwal.getNama_matakuliah());
        holder.rjm_dosen.setText(modelJadwal.getNama_dosen());
        holder.rjm_mulai.setText(modelJadwal.getJam_mulai());
        holder.rjm_ruangan.setText(modelJadwal.getNama_ruangan());
        holder.rjm_durasi.setText(durasi);
    }

    @Override
    public int getItemCount() {
        return listJadwal.size();
    }

    class HolderData extends RecyclerView.ViewHolder{

        TextView rjm_matakuliah, rjm_dosen, rjm_mulai, rjm_ruangan, rjm_durasi;
        HolderData(@NonNull View v) {
            super(v);
            rjm_matakuliah = v.findViewById(R.id.rpm_matakuliah);
            rjm_dosen = v.findViewById(R.id.rpm_dosen);
            rjm_mulai = v.findViewById(R.id.rpm_jam);
            rjm_ruangan = v.findViewById(R.id.rpm_ruangan);
            rjm_durasi = v.findViewById(R.id.rpm_durasi);
        }
    }
}
