package com.example.mobiledevicemanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiledevicemanager.models.Alerts;

import java.util.List;

public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.AlertViewHolder> {

    private List<Alerts> alertList;

    public AlertsAdapter(List<Alerts> alertList) {
        this.alertList = alertList;
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alert, parent, false);
        return new AlertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        Alerts alert = alertList.get(position);
        holder.textViewAlertTitle.setText(alert.getTitle());
        holder.textViewAlertMessage.setText(alert.getMessage());
    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }

    public static class AlertViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAlertTitle;
        TextView textViewAlertMessage;

        public AlertViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAlertTitle = itemView.findViewById(R.id.textViewAlertTitle);
            textViewAlertMessage = itemView.findViewById(R.id.textViewAlertMessage);
        }
    }
}
