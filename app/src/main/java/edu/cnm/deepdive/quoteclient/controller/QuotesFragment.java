package edu.cnm.deepdive.quoteclient.controller;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import edu.cnm.deepdive.quoteclient.R;
import edu.cnm.deepdive.quoteclient.view.QuoteRecyclerAdapter;
import edu.cnm.deepdive.quoteclient.viewmodel.MainViewModel;
import java.util.UUID;

public class QuotesFragment extends Fragment {

  private RecyclerView quotesList;

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_quotes, container, false);
    quotesList = root.findViewById(R.id.quotes_list);
    FloatingActionButton addQuote = root.findViewById(R.id.add_quote);
    addQuote.setOnClickListener((v) -> editQuote(null));
    return root;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupViewModel();
  }

  private void setupViewModel() {
    @SuppressWarnings("ConstantConditions")
    MainViewModel viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
    viewModel.getQuotes().observe(getViewLifecycleOwner(), (quotes) -> {
      QuoteRecyclerAdapter adapter = new QuoteRecyclerAdapter(getContext(), quotes, (position, quote) ->
          editQuote(quote.getId()));
      quotesList.setAdapter(adapter);
    });
  }

  private void editQuote(UUID quoteId) {
    Log.d(getClass().getName(), String.valueOf(quoteId));
    QuoteEditFragment fragment = QuoteEditFragment.newInstance(quoteId);
    fragment.show(getParentFragmentManager(), fragment.getClass().getName());
  }

}