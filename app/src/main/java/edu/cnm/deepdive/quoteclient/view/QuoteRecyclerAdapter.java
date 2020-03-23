package edu.cnm.deepdive.quoteclient.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.cnm.deepdive.quoteclient.R;
import edu.cnm.deepdive.quoteclient.model.Quote;
import edu.cnm.deepdive.quoteclient.model.Source;
import edu.cnm.deepdive.quoteclient.view.QuoteRecyclerAdapter.Holder;
import java.util.List;

public class QuoteRecyclerAdapter extends RecyclerView.Adapter<Holder> {

  private final Context context;
  private final List<Quote> quotes;
  private final OnQuoteClickListener listener;

  public QuoteRecyclerAdapter(Context context, List<Quote> quotes, OnQuoteClickListener listener) {
    this.context = context;
    this.quotes = quotes;
    this.listener = listener;
  }

  @NonNull
  @Override
  public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View root = LayoutInflater.from(context).inflate(R.layout.item_quote, parent, false);
    return new Holder(root);
  }

  @Override
  public void onBindViewHolder(@NonNull Holder holder, int position) {
    holder.bind(position, quotes.get(position));
  }

  @Override
  public int getItemCount() {
    return quotes.size();
  }

  class Holder extends RecyclerView.ViewHolder {

    private final View clickView;
    private final TextView quoteText;
    private final TextView quoteSource;

    private Holder(View root) {
      super(root);
      clickView = root.findViewById(R.id.click_view);
      quoteText = root.findViewById(R.id.quote_text);
      quoteSource = root.findViewById(R.id.quote_source);
    }

    private void bind(int position, Quote quote) {
      quoteText.setText(context.getString(R.string.quote_format, quote.getText()));
      Source source = quote.getSource();
      String name = (source != null) ? source.getName() : null;
      String attribution = (name != null)
          ? context.getString(R.string.attribution_format, name)
          : context.getString(R.string.unattributed_source);
      quoteSource.setText(attribution);
      clickView.setOnClickListener((v) ->
          listener.onQuoteClick(getAdapterPosition(), quote));
    }

  }

  @FunctionalInterface
  public interface OnQuoteClickListener {

    void onQuoteClick(int position, Quote quote);

  }

}
