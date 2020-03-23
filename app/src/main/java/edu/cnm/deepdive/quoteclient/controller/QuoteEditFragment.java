package edu.cnm.deepdive.quoteclient.controller;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
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
import edu.cnm.deepdive.quoteclient.model.Quote;
import edu.cnm.deepdive.quoteclient.model.Source;
import edu.cnm.deepdive.quoteclient.viewmodel.MainViewModel;
import java.util.List;
import java.util.UUID;

public class QuoteEditFragment extends DialogFragment {

  private static final String ID_KEY = "id";

  private UUID id;
  private View root;
  private MainViewModel viewModel;
  private EditText quoteText;
  private AutoCompleteTextView sourceName;
  private List<Source> sources;
  private Quote quote;

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
    } else {
      id = null;
    }
    root = LayoutInflater.from(getContext()).inflate(R.layout.fragment_quote_edit, null, false);
    quoteText = root.findViewById(R.id.quote_text);
    sourceName = root.findViewById(R.id.source_name);
    quoteText.setText("");
    sourceName.setText("");
    //noinspection ConstantConditions
    return new Builder(getContext())
        .setIcon(R.drawable.ic_message_black_24dp)
        .setTitle(R.string.quote_details_title)
        .setView(root)
        .setPositiveButton(android.R.string.ok, (dlg, which) -> save())
        .setNegativeButton(android.R.string.cancel, (dlg, which) -> {})
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
    viewModel.getSources().observe(getViewLifecycleOwner(), (sources) -> {
      this.sources = sources;
      ArrayAdapter<Source> adapter =
          new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, sources);
      sourceName.setAdapter(adapter);
    });
    if (id != null) {
      viewModel.getQuote().observe(getViewLifecycleOwner(), (quote) -> {
        if (quote != null) {
          this.quote = quote;
          quoteText.setText(quote.getText());
          if (quote.getSource() != null) {
            sourceName.setText(quote.getSource().getName());
          }
        }
      });
      viewModel.setQuoteId(id);
    } else {
      quote = new Quote();
    }
  }

  private void save() {
    quote.setText(quoteText.getText().toString().trim());
    Source source = null;
    String name = sourceName.getText().toString().trim();
    if (!name.isEmpty()) {
      for (Source s : sources) {
        if (name.equalsIgnoreCase(s.getName())) {
          source = s;
          break;
        }
      }
      if (source == null) {
        source = new Source();
        source.setName(name);
      }
    }
    quote.setSource(source);
    viewModel.save(quote);
  }

}
