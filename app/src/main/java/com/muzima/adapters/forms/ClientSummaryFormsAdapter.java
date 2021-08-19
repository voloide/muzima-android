package com.muzima.adapters.forms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.muzima.R;
import com.muzima.model.DownloadedForm;

import java.util.List;

public class ClientSummaryFormsAdapter extends RecyclerView.Adapter<ClientSummaryFormsAdapter.ViewHolder> {

    private List<DownloadedForm> forms;
    private OnFormClickedListener formClickedListener;

    public ClientSummaryFormsAdapter(List<DownloadedForm> forms, OnFormClickedListener formClickedListener) {
        this.forms = forms;
        this.formClickedListener = formClickedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forms_layout, parent, false);
        return new ViewHolder(view, formClickedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DownloadedForm form = forms.get(position);
        holder.formNameTextView.setText(form.getName());
        holder.formDescriptionTextView.setText(form.getDescription());
    }

    @Override
    public int getItemCount() {
        return forms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View container;
        private TextView formNameTextView;
        private RecyclerView formTagRecyclerView;
        private TextView formDescriptionTextView;
        private OnFormClickedListener formClickedListener;

        public ViewHolder(@NonNull View itemView, OnFormClickedListener formClickedListener) {
            super(itemView);

            container = itemView.findViewById(R.id.item_form_layout);
            formNameTextView = itemView.findViewById(R.id.item_form_name_text_view);
            formTagRecyclerView = itemView.findViewById(R.id.item_form_tags_list_view);
            formDescriptionTextView = itemView.findViewById(R.id.item_form_description_text_view);

            this.formClickedListener = formClickedListener;

            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            formClickedListener.onFormClickedListener(getAdapterPosition());
        }
    }

    public interface OnFormClickedListener {
        void onFormClickedListener(int position);
    }
}
