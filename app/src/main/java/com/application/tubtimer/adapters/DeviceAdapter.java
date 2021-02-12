package com.application.tubtimer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adroitandroid.near.model.Host;
import com.application.tubtimer.R;
import com.application.tubtimer.activities.SearchActivity;
import com.application.tubtimer.connection.CommandManager;

import java.util.ArrayList;
import java.util.List;


public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_ITEM = 2;
    private List<Host> mParticipants;
    SearchActivity activity;

    public DeviceAdapter(SearchActivity activity) {
        mParticipants = new ArrayList<>();
        this.activity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        return new DeviceViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull final DeviceViewHolder holder, int position) {
        if (TYPE_HEADER == holder.mViewType) {
            holder.name.setText("Найденные устройства:");
        } else {
            Host host = mParticipants.get(position - 1);
            holder.name.setText(host.getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.commandManager.sendConnectRequest(mParticipants.get(holder.getAdapterPosition() - 1));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mParticipants.size() == 0 ? 0 : mParticipants.size() + 1;
    }

    public void setData(ArrayList<Host> hosts) {
        mParticipants = hosts;
        notifyDataSetChanged();
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {
        private final int mViewType;

        TextView name;

        public DeviceViewHolder(@NonNull View root, int mViewType) {
            super(root);
            name = root.findViewById(R.id.name_tv);
            this.mViewType = mViewType;
        }

        /*DeviceViewHolder(int viewType, @NonNull RowParticipantsBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
            this.mViewType = viewType;
        }*/
    }

}
