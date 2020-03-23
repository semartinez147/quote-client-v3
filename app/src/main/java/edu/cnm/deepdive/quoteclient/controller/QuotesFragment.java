package edu.cnm.deepdive.quoteclient.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import edu.cnm.deepdive.quoteclient.R;
import edu.cnm.deepdive.quoteclient.view.QuoteRecyclerAdapter;
import edu.cnm.deepdive.quoteclient.viewmodel.MainViewModel;

public class QuotesFragment extends Fragment {

  private RecyclerView quotesList;

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_quotes, container, false);
    quotesList = root.findViewById(R.id.quotes_list);
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
      // TODO Attach any appropriate listeners
      QuoteRecyclerAdapter adapter = new QuoteRecyclerAdapter(getContext(), quotes);
      quotesList.setAdapter(adapter);
    });
  }

}