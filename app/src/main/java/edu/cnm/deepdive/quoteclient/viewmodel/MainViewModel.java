package edu.cnm.deepdive.quoteclient.viewmodel;

import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;
import edu.cnm.deepdive.quoteclient.model.Content;
import edu.cnm.deepdive.quoteclient.model.Quote;
import edu.cnm.deepdive.quoteclient.service.GoogleSignInService;
import edu.cnm.deepdive.quoteclient.service.QuoteRepository;
import io.reactivex.disposables.CompositeDisposable;
import java.util.List;
import java.util.UUID;

public class MainViewModel extends ViewModel implements LifecycleObserver {

  private MutableLiveData<Quote> random;
  private MutableLiveData<Quote> daily;
  private MutableLiveData<List<Quote>> quotes;
  private MutableLiveData<List<Content>> contents;
  private MutableLiveData<Quote> quote;
  private final MutableLiveData<Throwable> throwable;
  private final QuoteRepository repository;
  private CompositeDisposable pending;

  public MainViewModel() {
    repository = QuoteRepository.getInstance();
    pending = new CompositeDisposable();
    random = new MutableLiveData<>();
    daily = new MutableLiveData<>();
    quotes = new MutableLiveData<>();
    quote = new MutableLiveData<>();
    contents = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    refreshDaily();
    refreshQuotes();
    refreshContents();
  }

  public LiveData<Quote> getRandom() {
    return random;
  }

  public LiveData<Quote> getDaily() {
    return daily;
  }

  public LiveData<List<Quote>> getQuotes() {
    return quotes;
  }

  public LiveData<Quote> getQuote() {
    return quote;
  }

  public LiveData<List<Content>> getContents() {
    return contents;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  public void refreshRandom() {
    throwable.setValue(null);
    GoogleSignInService.getInstance().refresh()
        .addOnSuccessListener((account) -> {
          pending.add(
              repository.getRandom(account.getIdToken())
                  .subscribe(
                      random::postValue,
                      throwable::postValue
                  )
          );
        })
        .addOnFailureListener(throwable::postValue);
  }

  public void refreshDaily() {
    throwable.setValue(null);
    GoogleSignInService.getInstance().refresh()
        .addOnSuccessListener((account) -> {
          pending.add(
              repository.getQuoteOfToday(account.getIdToken())
                  .subscribe(
                      daily::postValue,
                      throwable::postValue
                  )
          );
        })
        .addOnFailureListener(throwable::postValue);
  }

  public void refreshQuotes() {
    throwable.setValue(null);
    GoogleSignInService.getInstance().refresh()
        .addOnSuccessListener((account) -> {
          pending.add(
              repository.getAllQuotes(account.getIdToken())
                  .subscribe(
                      quotes::postValue,
                      throwable::postValue
                  )
          );
        })
        .addOnFailureListener(throwable::postValue);
  }

  public void refreshContents() {
    throwable.setValue(null);
    GoogleSignInService.getInstance().refresh()
        .addOnSuccessListener((account) -> {
          pending.add(
              repository.getAllContent(account.getIdToken())
                  .subscribe(
                      contents::postValue,
                      throwable::postValue
                  )
          );
        })
        .addOnFailureListener(throwable::postValue);
  }

  public void save(Quote quote) {
    throwable.setValue(null);
    GoogleSignInService.getInstance().refresh()
        .addOnSuccessListener((account) -> {
          pending.add(
              repository.save(account.getIdToken(), quote)
                  .subscribe(
                      () -> {
                        refreshDaily();
                        refreshContents();
                        refreshQuotes();
                      },
                      throwable::postValue
                  )
          );
        })
        .addOnFailureListener(throwable::postValue);
  }

  public void setQuoteId(UUID id) {
    throwable.setValue(null);
    GoogleSignInService.getInstance().refresh()
        .addOnSuccessListener(
            (account) -> pending.add(
                repository.get(account.getIdToken(), id)
                    .subscribe(
                        quote::postValue,
                        throwable::postValue
                    )
            )
        )
        .addOnFailureListener(throwable::postValue);
  }

  @OnLifecycleEvent(Event.ON_STOP)
  private void clearPending() {
    pending.clear();
  }

}