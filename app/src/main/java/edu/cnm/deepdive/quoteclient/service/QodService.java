package edu.cnm.deepdive.quoteclient.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.cnm.deepdive.quoteclient.BuildConfig;
import edu.cnm.deepdive.quoteclient.model.Quote;
import edu.cnm.deepdive.quoteclient.model.Source;
import io.reactivex.Single;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface QodService {

  @GET("quotes/random")
  Single<Quote> getRandom(@Header("Authorization") String oauthHeader);

  @GET("quotes/qod")
  Single<Quote> getQuoteOfDay(
      @Header("Authorization") String oauthHeader, @Query("date") String date);

  @GET("quotes")
  Single<List<Quote>> getAll(@Header("Authorization") String oauthHeader);

  @GET("quotes/{id}")
  Single<Quote> get(@Header("Authorization") String oauthHeader, @Path("id") UUID id);

  @GET("sources")
  Single<List<Source>> getAllSources(
      @Header("Authorization") String oauthHeader, @Query("includeNull") boolean includeNull);

  @POST("quotes")
  Single<Quote> post(@Header("Authorization") String oauthHeader, @Body Quote quote);

  @PUT("quotes/{id}")
  Single<Quote> put(
      @Header("Authorization") String oauthHeader, @Body Quote quote, @Path("id") UUID id);

  static QodService getInstance() {
    return InstanceHolder.INSTANCE;
  }

  class InstanceHolder {

    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final QodService INSTANCE;

    static {
      Gson gson  = new GsonBuilder()
          .setDateFormat(TIMESTAMP_FORMAT)
          .excludeFieldsWithoutExposeAnnotation()
          .create();
      HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
      interceptor.setLevel(Level.BODY);
      OkHttpClient client = new OkHttpClient.Builder()
          .readTimeout(60, TimeUnit.SECONDS)
          .addInterceptor(interceptor)
          .build();
      Retrofit retrofit = new Retrofit.Builder()
          .addConverterFactory(GsonConverterFactory.create(gson))
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .client(client)
          .baseUrl(BuildConfig.BASE_URL)
          .build();
      INSTANCE = retrofit.create(QodService.class);
    }

  }

}
