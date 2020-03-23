package edu.cnm.deepdive.quoteclient.service;

import android.annotation.SuppressLint;
import edu.cnm.deepdive.quoteclient.model.Content;
import edu.cnm.deepdive.quoteclient.model.Quote;
import edu.cnm.deepdive.quoteclient.model.Source;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class QuoteRepository {

  private static final int NETWORK_POOL_SIZE = 10;
  private static final String OAUTH_HEADER_FORMAT = "Bearer %s";
  private static final String ISO_DATE_FORMAT = "yyyy-MM-dd";

  private final QodService proxy;
  private final Executor networkPool;
  private final DateFormat formatter;

  @SuppressLint("SimpleDateFormat")
  private QuoteRepository() {
    proxy = QodService.getInstance();
    networkPool = Executors.newFixedThreadPool(NETWORK_POOL_SIZE);
    formatter = new SimpleDateFormat(ISO_DATE_FORMAT);
  }

  public static QuoteRepository getInstance() {
    return InstanceHolder.INSTANCE;
  }

  public Single<Quote> getRandom(String token) {
    return proxy.getRandom(String.format(OAUTH_HEADER_FORMAT, token))
        .subscribeOn(Schedulers.from(networkPool));
  }

  public Single<Quote> getQuoteOfToday(String token) {
    return getQuoteOfDay(token, new Date());
  }

  public Single<Quote> getQuoteOfDay(String token, Date date) {
    return proxy.getQuoteOfDay(String.format(OAUTH_HEADER_FORMAT, token), formatter.format(date))
        .subscribeOn(Schedulers.from(networkPool));
  }

  public Single<List<Quote>> getAllQuotes(String token) {
    return proxy.getAll(String.format(OAUTH_HEADER_FORMAT, token))
        .subscribeOn(Schedulers.from(networkPool));
  }

  public Single<List<Source>> getAllSources(String token, boolean includeNull) {
    return proxy.getAllSources(String.format(OAUTH_HEADER_FORMAT, token), includeNull)
        .subscribeOn(Schedulers.from(networkPool));
  }

  public Completable save(String token, Quote quote) {
    if (quote.getId() == null) {
      return Completable.fromSingle(
          proxy.post(String.format(OAUTH_HEADER_FORMAT, token), quote)
              .subscribeOn(Schedulers.from(networkPool))
      );
    } else {
      return Completable.fromSingle(
          proxy.put(String.format(OAUTH_HEADER_FORMAT, token), quote, quote.getId())
              .subscribeOn(Schedulers.from(networkPool))
      );
    }
  }

  public Single<Quote> get(String token, UUID id) {
    return proxy.get(String.format(OAUTH_HEADER_FORMAT, token), id)
        .subscribeOn(Schedulers.from(networkPool));
  }

  public Single<List<Content>> getAllContent(String token) {
    return getAllSources(token, true)
        .subscribeOn(Schedulers.io())
        .map((sources) -> {
          List<Content> combined = new ArrayList<>();
          for (Source source : sources) {
            combined.add(source);
            Collections.addAll(combined, source.getQuotes());
          }
          return combined;
        });
  }

  private static class InstanceHolder {

    private static final QuoteRepository INSTANCE = new QuoteRepository();

  }

}
