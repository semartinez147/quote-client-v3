package edu.cnm.deepdive.quoteclient.controller;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;
import edu.cnm.deepdive.quoteclient.R;
import edu.cnm.deepdive.quoteclient.viewmodel.MainViewModel;
import java.util.UUID;

public class QuoteEditFragment extends DialogFragment {

  private static final String ID_KEY = "id";

  private UUID id;
  private View root;
  private MainViewModel viewModel;

  public static QuoteEditFragment newInstance(UUID id) {
    QuoteEditFragment fragment = new QuoteEditFragment();
    Bundle args = new Bundle();
    args.putSerializable(ID_KEY, id);
    fragment.setArguments(args);
    return fragment;
  }

  @SuppressLint("InflateParams")
  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    if (getArguments() != null) {
      id = (UUID) getArguments().getSerializable(ID_KEY);
    }
    root = LayoutInflater.from(getContext()).inflate(R.layout.fragment_quote_edit, null, false);
    EditText quoteText = root.findViewById(R.id.quote_text);
    AutoCompleteTextView sourceName = root.findViewById(R.id.source_name);
    //noinspection ConstantConditions
    return new Builder(getContext())
        .setTitle(R.string.quote_details_title)
        .setView(root)
        .setPositiveButton(android.R.string.ok, (dlg, which) -> {
          // TODO Use viewModel to save Quote object.
        })
        .create();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return root;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
  }

}
