package com.lol;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lol.boring.Event;
import com.lol.boring.GsonFactory;
import com.lol.boring.UserInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

public class Api {

    public Observable<UserInfo> userInfo(final Context context) {
        return Observable.create(new Observable.OnSubscribeFunc<UserInfo>() {
            @Override
            public Subscription onSubscribe(Observer<? super UserInfo> observer) {
                Log.i("meow", "loading user data");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                String state = prefs.getString("timeline-state", "{\"cardsRead\":{}}");
                Gson gson = GsonFactory.getGson();
                UserInfo userInfo = gson.fromJson(state, UserInfo.class);
                observer.onNext(userInfo);

                // this is odd, but because we zip these with events, we don't want to complete.
                // i need to find a better way to combine the two observables.
                // observer.onCompleted();

                return Subscriptions.empty();
            }
        });
    }

    public Observable<List<Event>> events() {
        return Observable.create(new Observable.OnSubscribeFunc<List<Event>>() {
            @Override
            public Subscription onSubscribe(Observer<? super List<Event>> observer) {
                try {
                    // TODO: 304s
                    Log.i("meow", "calling api");
                    URL url = new URL("http://sochi.staging.pairshaped.ca/api/events");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    Log.i("meow", "reading response");
                    String body = readToEnd(conn.getInputStream());
                    Gson gson = GsonFactory.getGson();
                    Type type = new TypeToken<List<Event>>() {
                    }.getType();

                    Log.i("meow", "parsing events");
                    List<Event> events = gson.fromJson(body, type);
                    observer.onNext(events);
                    observer.onCompleted();
                    conn.disconnect();
                } catch (MalformedURLException e) {
                    observer.onError(e);
                } catch (IOException e) {
                    observer.onError(e);
                }

                return Subscriptions.empty();
            }
        });
    }

    public static String readToEnd(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
}
